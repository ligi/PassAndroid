package org.ligi.passandroid.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View.GONE
import kotlinx.android.synthetic.main.activity_scan.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.ligi.passandroid.R
import org.ligi.passandroid.events.ScanFinishedEvent
import org.ligi.passandroid.events.ScanProgressEvent

class PassScanActivity : PassAndroidActivity() {

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onScanProgress(event: ScanProgressEvent) {
        progress_text.text = event.message
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onScanFinished(event: ScanFinishedEvent) {
        progress_container.visibility = GONE
        val message = getString(R.string.scan_finished_dialog_text, event.foundPasses.size)
        AlertDialog.Builder(this)
                .setTitle(R.string.scan_finished_dialog_title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    finish()
                }
                .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_scan)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val intent = Intent(this, SearchPassesIntentService::class.java)
        startService(intent)
    }

    override fun onResume() {
        super.onResume()

        bus.register(this)

    }

    override fun onPause() {
        bus.unregister(this)
        super.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }
}