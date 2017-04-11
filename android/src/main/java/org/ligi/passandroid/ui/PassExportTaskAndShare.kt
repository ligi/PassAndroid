package org.ligi.passandroid.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Handler
import android.support.annotation.UiThread
import android.support.v4.content.FileProvider
import android.widget.Toast
import org.ligi.passandroid.App
import org.ligi.passandroid.R
import java.io.File

internal open class PassExportTaskAndShare(protected val activity: Activity, val inputPath: File)  {

    @UiThread
    fun execute() {
        val file = File(activity.filesDir, "share/share.espass") // important - the FileProvider must be configured for this path
        val passExporter = PassExporter(inputPath, file)
        val progress_dialog = ProgressDialog(activity)
        progress_dialog.setTitle(R.string.preparing_pass)
        progress_dialog.setMessage(activity.getString(R.string.please_wait))
        progress_dialog.show()

        val handler = Handler()
        Thread(Runnable {
            passExporter.export()
            handler.post {
                if (!activity.isFinishing && progress_dialog.isShowing) {
                    progress_dialog.dismiss()
                }

                if (passExporter.exception != null) {
                    App.tracker.trackException("passExporterException", passExporter.exception, false)
                    Toast.makeText(activity, "could not export pass " + passExporter.exception, Toast.LENGTH_LONG).show()
                } else {
                    val uriForFile = FileProvider.getUriForFile(activity, activity.getString(R.string.authority_fileprovider), file)
                    val it = Intent(Intent.ACTION_SEND)
                    it.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.passbook_is_shared_subject))
                    it.putExtra(Intent.EXTRA_STREAM, uriForFile)
                    it.type = "application/vnd.espass-espass"
                    activity.startActivity(Intent.createChooser(it, activity.getString(R.string.passbook_share_chooser_title)))
                }
            }
        }).start()
    }

}