package org.ligi.passandroid.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewConfiguration
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
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
import java.io.IOException

@SuppressLint("Registered")
@RuntimePermissions
open class PassViewActivityBase : PassAndroidActivity() {

    lateinit var currentPass: Pass
    private var fullBrightnessSet = false

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
                createShortcutWithPermissionCheck()
                true
            }

            R.id.menu_update -> {
                Thread(UpdateAsync()).start()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @NeedsPermission("com.android.launcher.permission.INSTALL_SHORTCUT")
    fun createShortcut() {
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