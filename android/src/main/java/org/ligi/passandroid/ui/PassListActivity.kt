package org.ligi.passandroid.ui

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import kotlinx.android.synthetic.main.pass_list.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.ligi.kaxt.setButton
import org.ligi.kaxt.startActivityFromClass
import org.ligi.kaxt.startActivityFromURL
import org.ligi.passandroid.R
import org.ligi.passandroid.events.PassStoreChangeEvent
import org.ligi.passandroid.events.ScanFinishedEvent
import org.ligi.passandroid.events.ScanProgressEvent
import org.ligi.passandroid.functions.createAndAddEmptyPass
import org.ligi.passandroid.model.PassStoreProjection
import org.ligi.passandroid.model.State
import org.ligi.snackengage.SnackEngage
import org.ligi.snackengage.snacks.BaseSnack
import org.ligi.snackengage.snacks.DefaultRateSnack
import org.ligi.tracedroid.TraceDroid
import org.ligi.tracedroid.sending.TraceDroidEmailSender
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions

private const val OPEN_FILE_READ_REQUEST_CODE = 1000
private const val VERSION_STARTING_TO_SUPPORT_STORAGE_FRAMEWORK = 19

@RuntimePermissions
class PassListActivity : PassAndroidActivity() {

    private val drawerToggle by lazy { ActionBarDrawerToggle(this, drawer_layout, R.string.drawer_open, R.string.drawer_close) }

    private val adapter by lazy { PassTopicFragmentPagerAdapter(passStore.classifier, supportFragmentManager) }
    private val pd by lazy { ProgressDialog(this) }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onScanProgress(event: ScanProgressEvent) {
        if (pd.isShowing) {
            pd.setMessage(event.message)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onScanFinished(event: ScanFinishedEvent) {

        if (pd.isShowing) {
            val message = getString(R.string.scan_finished_dialog_text, event.foundPasses.size)
            pd.dismiss()
            AlertDialog.Builder(this@PassListActivity).setTitle(R.string.scan_finished_dialog_title).setMessage(message).setPositiveButton(android.R.string.ok, null).show()
        }

    }

    @TargetApi(16)
    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    internal fun showDeniedFor() {
        Snackbar.make(fam, "no permission to scan", Snackbar.LENGTH_INDEFINITE).show()
    }

    @TargetApi(16)
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun scan() {
        val intent = Intent(this, SearchPassesIntentService::class.java)
        startService(intent)

        pd.setTitle(R.string.scan_progressdialog_title)
        pd.setMessage(getString(R.string.scan_progressdialog_message))
        pd.setCancelable(false)
        pd.isIndeterminate = true
        pd.setButton(DialogInterface.BUTTON_NEUTRAL, R.string.scan_dialog_send_background_button) {
            this@PassListActivity.finish()
        }
        pd.setButton(DialogInterface.BUTTON_POSITIVE, android.R.string.cancel) {
            stopService(intent)
        }
        pd.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PassListActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults)
    }

    @TargetApi(VERSION_STARTING_TO_SUPPORT_STORAGE_FRAMEWORK)
    internal fun onAddOpenFileClick() {
        try {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*" // tried with octet stream - no use
            startActivityForResult(intent, OPEN_FILE_READ_REQUEST_CODE)
        } catch (e: ActivityNotFoundException) {
            Snackbar.make(fam, "Unavailable", Snackbar.LENGTH_LONG).show()
        }

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == OPEN_FILE_READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                val targetIntent = Intent(this, PassImportActivity::class.java)
                targetIntent.data = resultData.data
                startActivity(targetIntent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.pass_list)

        setSupportActionBar(toolbar)


        // don't want too many windows in worst case - so check for errors first
        if (TraceDroid.getStackTraceFiles().isNotEmpty()) {
            tracker.trackEvent("ui_event", "send", "stacktraces", null)
            if (settings.doTraceDroidEmailSend()) {
                TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this)
            }
        } else { // if no error - check if there is a new version of the app
            tracker.trackEvent("ui_event", "processFile", "updatenotice", null)

            SnackEngage.from(fam).withSnack(DefaultRateSnack().withDuration(BaseSnack.DURATION_INDEFINITE))
                    .build().engageWhenAppropriate()
        }

        drawer_layout.addDrawerListener(drawerToggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        view_pager.adapter = adapter

        if (adapter.count > 0) {
            view_pager.currentItem = State.lastSelectedTab
        }

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                State.lastSelectedTab = position
                supportInvalidateOptionsMenu()
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        passStore.syncPassStoreWithClassifier(getString(R.string.topic_new))

        onPassStoreChangeEvent(null)

        fab_action_create_pass.setOnClickListener {
            val pass = createAndAddEmptyPass(passStore, resources)

            fam.collapse()
            startActivityFromClass(PassEditActivity::class.java)

            val newTitle = if (tab_layout.selectedTabPosition < 0) {
                getString(R.string.topic_new)
            } else {
                adapter.getPageTitle(tab_layout.selectedTabPosition)
            }

            passStore.classifier.moveToTopic(pass, newTitle)
        }

        fab_action_scan.setOnClickListener {
            PassListActivityPermissionsDispatcher.scanWithCheck(this)
            fam.collapse()
        }

        fab_action_demo_pass.setOnClickListener {
            startActivityFromURL("http://espass.it/examples")
            fam.collapse()
        }

        if (Build.VERSION.SDK_INT >= VERSION_STARTING_TO_SUPPORT_STORAGE_FRAMEWORK) {
            fab_action_open_file.setOnClickListener {
                onAddOpenFileClick()
            }
            fab_action_open_file.visibility = VISIBLE
        }
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_help -> {
            startActivityFromClass(HelpActivity::class.java)
            true
        }

        R.id.menu_emptytrash -> {
            AlertDialog.Builder(this).setMessage(getString(R.string.empty_trash_dialog_message)).setIcon(R.drawable.ic_alert_warning).setTitle(getString(R.string.empty_trash_dialog_title)).setPositiveButton(R.string.emtytrash_label) { dialog, which ->
                val passStoreProjection = PassStoreProjection(passStore,
                        getString(R.string.topic_trash),
                        null)

                for (pass in passStoreProjection.passList) {
                    passStore.deletePassWithId(pass.id)
                }
            }.setNegativeButton(android.R.string.cancel, null).show()
            true
        }

        else -> drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        bus.register(this)

        adapter.notifyDataSetChanged()
        onPassStoreChangeEvent(null)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.menu_emptytrash).isVisible = adapter.count > 0 && adapter.getPageTitle(view_pager.currentItem) == getString(R.string.topic_trash)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_pass_list_view, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onPause() {
        bus.unregister(this)
        super.onPause()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPassStoreChangeEvent(passStoreChangeEvent: PassStoreChangeEvent?) {
        adapter.notifyDataSetChanged()

        setupWithViewPagerIfNeeded()

        supportInvalidateOptionsMenu()

        val empty = passStore.classifier.topicByIdMap.isEmpty()
        emptyView.visibility = if (empty) View.VISIBLE else View.GONE
        val onlyDefaultTopicExists = passStore.classifier.getTopics().all { it == getString(R.string.topic_new) }
        tab_layout.visibility = if (onlyDefaultTopicExists) View.GONE else View.VISIBLE
    }

    private fun setupWithViewPagerIfNeeded() {
        if (!areTabLayoutAndViewPagerInSync()) {
            tab_layout.setupWithViewPager(view_pager)
        }
    }

    private fun areTabLayoutAndViewPagerInSync(): Boolean {

        if (adapter.count != tab_layout.tabCount) {
            return false
        }

        for (i in 0 until adapter.count) {
            val tabAt = tab_layout.getTabAt(i)
            if (tabAt == null || adapter.getPageTitle(i) != tabAt.text) {
                return false
            }
        }
        return true

    }

    override fun onBackPressed() {
        when {
            drawer_layout.isDrawerOpen(GravityCompat.START) -> drawer_layout.closeDrawer(GravityCompat.START)
            fam.isExpanded -> fam.collapse()
            else -> super.onBackPressed()
        }
    }

}