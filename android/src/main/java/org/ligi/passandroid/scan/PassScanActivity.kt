package org.ligi.passandroid.scan

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View.GONE
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.ligi.passandroid.R
import org.ligi.passandroid.databinding.ActivityScanBinding
import org.ligi.passandroid.scan.events.DirectoryProcessed
import org.ligi.passandroid.scan.events.PassScanEventChannelProvider
import org.ligi.passandroid.scan.events.ScanFinished
import org.ligi.passandroid.ui.PassAndroidActivity

class PassScanActivity : PassAndroidActivity() {

    private val progressChannelProvider: PassScanEventChannelProvider by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        lifecycleScope.launch {
            for (event in progressChannelProvider.channel.openSubscription()) {
                when (event) {
                    is DirectoryProcessed -> binding.progressText.text = event.dir
                    is ScanFinished -> {
                        binding.progressContainer.visibility = GONE
                        val message = getString(R.string.scan_finished_dialog_text, event.foundPasses.size)
                        AlertDialog.Builder(this@PassScanActivity)
                                .setTitle(R.string.scan_finished_dialog_title)
                                .setMessage(message)
                                .setPositiveButton(android.R.string.ok) { _, _ ->
                                    finish()
                                }
                                .show()
                    }
                }

            }
        }

        val intent = Intent(this, SearchPassesIntentService::class.java)
        startService(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }
}