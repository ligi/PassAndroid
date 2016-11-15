package org.ligi.passandroid.ui

import android.app.ProgressDialog
import android.net.Uri
import android.os.AsyncTask
import org.ligi.kaxt.startActivityFromClass
import org.ligi.passandroid.R
import org.ligi.passandroid.model.InputStreamWithSource
import org.ligi.passandroid.ui.UnzipPassController.InputStreamUnzipControllerSpec

internal class ImportAndShowAsyncTask(val passImportActivity: PassImportActivity, val intent_uri: Uri) : AsyncTask<Void, Void, InputStreamWithSource?>() {

    private val progressDialog by lazy {
        val progressDialog = ProgressDialog(passImportActivity)
        progressDialog.setMessage(passImportActivity.getString(R.string.please_wait))
        progressDialog.setCancelable(false)
        progressDialog
    }

    override fun doInBackground(vararg params: Void): InputStreamWithSource? {
        return InputStreamProvider.fromURI(passImportActivity, intent_uri)
    }

    override fun onPreExecute() {
        progressDialog.show()
        super.onPreExecute()
    }

    override fun onPostExecute(result: InputStreamWithSource?) {
        super.onPostExecute(result)

        if (result == null) {

            if (!passImportActivity.isFinishing && progressDialog.isShowing) {
                try {
                    progressDialog.dismiss()
                } catch (ignored: Exception) {

                }

            }
            passImportActivity.finish()
            //TODO show some error here?!
            return  // no result -> no work here
        }

        if (passImportActivity.isFinishing) { // finish with no UI/Dialogs
            // let's do it silently TODO check if we need to jump to a service here as the activity is dying
            val spec = InputStreamUnzipControllerSpec(result, passImportActivity.application, passImportActivity.passStore, null, null)
            UnzipPassController.processInputStream(spec)
            return
        }


        UnzipPassDialog.show(result, passImportActivity, passImportActivity.passStore) { path ->
            // TODO this is kind of a hack - there should be a better way
            val id = path.split("/".toRegex()).dropLastWhile(String::isEmpty).toTypedArray().last()

            val passbookForId = passImportActivity.passStore.getPassbookForId(id)
            passImportActivity.passStore.currentPass = passbookForId

            passImportActivity.passStore.classifier.moveToTopic(passbookForId!!, passImportActivity.getString(R.string.topic_new))

            passImportActivity.startActivityFromClass(PassViewActivity::class.java)
            passImportActivity.finish()
        }

    }
}
