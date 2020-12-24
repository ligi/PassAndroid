package org.ligi.passandroid.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Handler
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.core.content.FileProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ligi.passandroid.R
import org.ligi.passandroid.Tracker
import java.io.File

internal open class PassExportTaskAndShare(
        protected val activity: Activity,
        private val inputPath: File
) : KoinComponent {

    val tracker: Tracker by inject()
    @UiThread
    fun execute() {
        val file = File(activity.filesDir, "share/share.espass") // important - the FileProvider must be configured for this path
        val passExporter = PassExporter(inputPath, file)
        val progressDialog = ProgressDialog(activity)
        progressDialog.setTitle(R.string.preparing_pass)
        progressDialog.setMessage(activity.getString(R.string.please_wait))
        progressDialog.show()

        val handler = Handler()
        Thread {
            passExporter.export()
            handler.post {
                if (!activity.isFinishing && progressDialog.isShowing) {
                    progressDialog.dismiss()
                }

                if (passExporter.exception != null) {
                    tracker.trackException("passExporterException", passExporter.exception!!, false)
                    Toast.makeText(activity, "could not export pass: " + passExporter.exception, Toast.LENGTH_LONG).show()
                } else {
                    val uriForFile = FileProvider.getUriForFile(activity, activity.getString(R.string.authority_fileprovider), file)
                    val it = Intent(Intent.ACTION_SEND)
                    it.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.passbook_is_shared_subject))
                    it.putExtra(Intent.EXTRA_STREAM, uriForFile)
                    it.type = "application/vnd.espass-espass"
                    activity.startActivity(Intent.createChooser(it, activity.getString(R.string.passbook_share_chooser_title)))
                }
            }
        }.start()
    }

}