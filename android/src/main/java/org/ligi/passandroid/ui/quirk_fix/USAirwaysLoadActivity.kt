package org.ligi.passandroid.ui.quirk_fix

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog

import org.ligi.passandroid.ui.PassAndroidActivity
import org.ligi.passandroid.ui.PassImportActivity

class USAirwaysLoadActivity : PassAndroidActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = intent.data
        if (data == null){
            AlertDialog.Builder(this).setTitle("Workaround failed")
                    .setMessage(
                            "The URL PassAndroid tried to work around failed :-( some companies just send PassBooks to Apple Devices - this was an attempt to workaround this." + "Unfortunately it failed - perhaps there where changes on the serverside - you can open the site with your browser now - to see it in PassAndroid in future again it would help if you can send me the pass")
                    .setPositiveButton("Browser") { dialog, which ->
                        tracker.trackException("URLRewrite with invalid activity", false)
                        val intent = Intent(this@USAirwaysLoadActivity, OpenIphoneWebView::class.java)
                        intent.data = data
                        startActivity(intent)
                    }
                    .setNeutralButton("send") { dialog, which ->
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.putExtra(Intent.EXTRA_SUBJECT, "PassAndroid: URLRewrite Problem")
                        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("ligi@ligi.de"))
                        intent.putExtra(Intent.EXTRA_TEXT, data.toString())
                        intent.type = "text/plain"

                        startActivity(Intent.createChooser(intent, "How to send Link?"))
                        finish()
                    }
                    .setNegativeButton("cancel") { dialog, which -> this@USAirwaysLoadActivity.finish() }
                    .show()

            return
        }
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
