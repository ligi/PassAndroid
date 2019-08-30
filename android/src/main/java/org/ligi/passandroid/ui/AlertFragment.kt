package org.ligi.passandroid.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log

import androidx.fragment.app.Fragment

import org.ligi.passandroid.Tracker
import org.ligi.passandroid.ui.quirk_fix.OpenIphoneWebView

class AlertFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val tracker = (activity!! as PassAndroidActivity).tracker
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Workaround failed")
        builder.setMessage("The URL PassAndroid tried to work around failed :-( some companies just send PassBooks to Apple Devices - this was an attempt to workaround this." + "Unfortunately it failed - perhaps there where changes on the serverside - you can open the site with your browser now - to see it in PassAndroid in future again it would help if you can send me the pass")
        builder.setPositiveButton("Browser") { dialog, which ->
            tracker.trackException(activity!!.localClassName + "with invalid activity", false)
            val intent = Intent(activity, OpenIphoneWebView::class.java)
            intent.data = activity!!.intent.data
            startActivity(intent)
        }
        builder.setNeutralButton("send") { dialog, which ->
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_SUBJECT, "PassAndroid: URLRewrite Problem")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("ligi@ligi.de"))
            if(activity!!.intent.data != null)
            {
                intent.putExtra(Intent.EXTRA_TEXT, activity!!.intent.data!!.toString())
            }
            else{
                intent.putExtra(Intent.EXTRA_TEXT, "null")
            }
            intent.type = "text/plain"
        }
        builder.setNegativeButton("cancle") { dialog, which -> activity!!.finish() }
        val dialog = builder.create()
        dialog.show()
    }
}
