package org.ligi.passandroid.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewConfiguration
import android.view.WindowManager
import android.widget.RemoteViews
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.scale
import com.google.android.material.snackbar.Snackbar
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.ligi.passandroid.R
import org.ligi.passandroid.model.InputStreamWithSource
import org.ligi.passandroid.model.PassBitmapDefinitions.BITMAP_ICON
import org.ligi.passandroid.model.State
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.ui.UnzipPassController.InputStreamUnzipControllerSpec
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import permissions.dispatcher.ktx.constructPermissionsRequest
import java.io.IOException

@SuppressLint("Registered")
open class PassViewActivityBase : PassAndroidActivity() {

    lateinit var currentPass: Pass
    private var fullBrightnessSet = false
    private val NOTIFICATION_CHANNEL_ID = "passnotifications"
    val PASS_NOTIFICATION_ID = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // a little hack because I strongly disagree with the style guide here
        // ;-)
        // not having the Actionbar overflow menu also with devices with hardware
        // key really helps discoverability
        // http://stackoverflow.com/questions/9286822/how-to-force-use-of-overflow-menu-on-devices-with-menu-button
        try {
            val config = ViewConfiguration.get(this)
            val menuKeyField = ViewConfiguration::class.java.getDeclaredField("sHasPermanentMenuKey")
            menuKeyField.isAccessible = true
            menuKeyField.setBoolean(config, false)
        } catch (ex: Exception) {
            // Ignore - but at least we tried ;-)
        }

        updateCurrentPass()
    }

    override fun onPause() {
        super.onPause()
        State.lastSelectedPassUUID = currentPass.id
    }

    override fun onResume() {
        super.onResume()

        configureActionBar()

        if (settings.isAutomaticLightEnabled()) {
            setToFullBrightness()
        }
    }

    private fun updateCurrentPass() {
        val uuid = intent.getStringExtra(EXTRA_KEY_UUID)

        if (uuid != null) {
            passStore.currentPass = passStore.getPassbookForId(uuid)
        }

        if (passStore.currentPass == null) {
            passStore.currentPass = passStore.getPassbookForId(State.lastSelectedPassUUID)
        }

        if (passStore.currentPass == null) {
            tracker.trackException("pass not present in $this", false)
            finish()
            return
        }

        currentPass = passStore.currentPass!!
    }

    protected fun configureActionBar() {
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    protected open fun refresh() {
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_pass_view, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val res = super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.menu_light).isVisible = !fullBrightnessSet
        menu.findItem(R.id.menu_print).isVisible = Build.VERSION.SDK_INT >= 19
        return res
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (PassMenuOptions(this, currentPass).process(item)) {
            return true
        }

        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            R.id.menu_light -> {
                setToFullBrightness()
                true
            }

            R.id.install_shortcut -> {
                createShortcut()
                true
            }

            R.id.menu_update -> {
                Thread(UpdateAsync()).start()
                true
            }
            R.id.menu_notification -> {
                createNotification()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun createShortcut() {
        constructPermissionsRequest(Manifest.permission.INSTALL_SHORTCUT) {
            val passBitmap = currentPass.getBitmap(passStore, BITMAP_ICON)
            val shortcutIcon = passBitmap?.scale(128, 128, filter = true) ?: BitmapFactory.decodeResource(resources, R.drawable.ic_launcher)
            val name: CharSequence = currentPass.description.let {
                if (it.isNullOrEmpty()) "pass" else it
            }

            val targetIntent = Intent(this, PassViewActivity::class.java)
                .setAction(Intent.ACTION_MAIN)
                .putExtra(EXTRA_KEY_UUID, currentPass.id)
            val shortcutInfo = ShortcutInfoCompat.Builder(this, "shortcut$name")
                .setIntent(targetIntent)
                .setShortLabel(name)
                .setIcon(IconCompat.createWithBitmap(shortcutIcon))
                .build()
            ShortcutManagerCompat.requestPinShortcut(this, shortcutInfo, null)
        }.launch()
    }

    private fun createNotification() {
        val expandedView = RemoteViews(packageName, R.layout.notification_expanded)
        val notifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT > 25) {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "PassAndroid Pass Notification", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Notifications to quickly show and open PassAndroid passes"
            notifyManager.createNotificationChannel(channel)
        }

        val passNotificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        passNotificationBuilder.setSmallIcon(R.drawable.ic_theaters_notification)
                .setContentTitle(currentPass.description)
                .setContentText(getString(R.string.notification_text))

        val iconBitmap = currentPass.getBitmap(passStore, BITMAP_ICON)
        if (iconBitmap != null) {
            passNotificationBuilder.setLargeIcon(iconBitmap)
        }

        val barCodeBitmap = currentPass.barCode?.getBitmap(this.resources)!!.bitmap
        if (barCodeBitmap != null) {
            val height = (240 * this.resources.displayMetrics.density).toInt()
            val originalHeight = barCodeBitmap.height
            val originalWidth = barCodeBitmap.width
            val scale = height / originalHeight
            val width = (originalWidth * scale).toInt()
            val scaledBarCodeBitmap = Bitmap.createScaledBitmap(barCodeBitmap, height, width, false)
            expandedView.setImageViewBitmap(R.id.image_view_expanded, scaledBarCodeBitmap)
            passNotificationBuilder.setCustomBigContentView(expandedView)
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
        }

        val intent = Intent(this, PassViewActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        passNotificationBuilder.setContentIntent(pendingIntent)
        passNotificationBuilder.setAutoCancel(true)
        notifyManager.notify(PASS_NOTIFICATION_ID, passNotificationBuilder.build())
    }

    inner class UpdateAsync : Runnable {

        private lateinit var dlg: ProgressDialog

        override fun run() {
            val pass = currentPass
            runOnUiThread {
                dlg = ProgressDialog(this@PassViewActivityBase)
                dlg.setMessage(getString(R.string.downloading_new_pass_version))
                dlg.show()
            }

            val client = OkHttpClient()

            val url = pass.webServiceURL + "/v1/passes/" + pass.passIdent + "/" + pass.serial
            val requestBuilder = Request.Builder().url(url)
            requestBuilder.addHeader("Authorization", "ApplePass " + pass.authToken)

            val request = requestBuilder.build()

            val response: Response
            try {
                response = client.newCall(request).execute()
                val body = response.body()
                if (body != null) {
                    val inputStreamWithSource = InputStreamWithSource(url, body.byteStream())
                    val spec = InputStreamUnzipControllerSpec(inputStreamWithSource, this@PassViewActivityBase, passStore,
                            MyUnzipSuccessCallback(dlg),
                            MyUnzipFailCallback(dlg))
                    spec.overwrite = true
                    UnzipPassController.processInputStream(spec)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    inner class MyUnzipFailCallback constructor(private val dlg: Dialog) : UnzipPassController.FailCallback {

        override fun fail(reason: String) {
            runOnUiThread {
                if (!isFinishing) {
                    dlg.dismiss()
                    AlertDialog.Builder(this@PassViewActivityBase).setMessage("Could not update pass :( $reason)")
                            .setPositiveButton(android.R.string.ok, null)
                            .show()
                }
            }

        }
    }

    inner class MyUnzipSuccessCallback constructor(private val dlg: Dialog) : UnzipPassController.SuccessCallback {

        override fun call(uuid: String) {
            runOnUiThread(Runnable {
                if (isFinishing) {
                    return@Runnable
                }
                dlg.dismiss()
                if (currentPass.id != uuid) {
                    passStore.deletePassWithId(currentPass.id)
                }
                val newPass = passStore.getPassbookForId(uuid)
                passStore.currentPass = newPass
                currentPass = passStore.currentPass!!
                refresh()

                Snackbar.make(window.decorView, R.string.pass_updated, Snackbar.LENGTH_LONG).show()
            })

        }

    }

    private fun setToFullBrightness() {
        val win = window
        val params = win.attributes
        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        win.attributes = params
        fullBrightnessSet = true
        invalidateOptionsMenu()
    }

    companion object {

        const val EXTRA_KEY_UUID = "uuid"

        fun mightPassBeAbleToUpdate(pass: Pass?): Boolean {
            return pass?.webServiceURL != null && pass.passIdent != null && pass.serial != null
        }
    }
}