package org.ligi.passandroid.ui.quirk_fix

import android.content.Intent
import android.net.Uri
import android.os.Bundle

import org.ligi.passandroid.ui.PassAndroidActivity
import org.ligi.passandroid.ui.PassImportActivity

class USAirwaysLoadActivity : PassAndroidActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = intent.data
        val url = data?.toString()?.removeSuffix("/") ?: ""

        val split = url.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val passId = split[split.size - 2] + "/" + split[split.size - 1]

        val redirectUrl = "http://prod.wap.ncrwebhost.mobi/mobiqa/wap/$passId/passbook"

        tracker.trackEvent("quirk_fix", "redirect", "usairways", null)
        val intent = Intent(this, PassImportActivity::class.java)
        intent.data = Uri.parse(redirectUrl)
        startActivity(intent)
        finish()
    }
}
