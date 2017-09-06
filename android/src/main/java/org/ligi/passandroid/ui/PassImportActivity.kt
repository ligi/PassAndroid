package org.ligi.passandroid.ui

import android.Manifest
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.salomonbrys.kodein.instance
import org.ligi.kaxt.dismissIfShowing
import org.ligi.kaxt.startActivityFromClass
import org.ligi.kaxtui.alert
import org.ligi.passandroid.App
import org.ligi.passandroid.R
import org.ligi.passandroid.Tracker
import org.ligi.passandroid.functions.fromURI
import org.ligi.passandroid.model.PassStore
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions


@RuntimePermissions
class PassImportActivity : AppCompatActivity() {

    val tracker: Tracker = App.kodein.instance()
    val passStore: PassStore = App.kodein.instance()


    private val progressDialog by lazy {
        ProgressDialog(this).apply {
            setMessage(getString(R.string.please_wait))
            setCancelable(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.data?.scheme == null) {
            tracker.trackException("invalid_import_uri", false)
            finish()
            return
        }

        progressDialog.show()

        doImport(false)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PassImportActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults)
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun doImport(withPermission: Boolean) {
        Thread({
            try {
                val fromURI = fromURI(this, intent.data)

                runOnUiThread({
                    progressDialog.dismissIfShowing()

                    if (fromURI == null) {
                        finish()
                        //TODO show some error here?!
                    } else {

                        if (isFinishing) {
                            // finish with no UI/Dialogs
                            // let's do it silently TODO check if we need to jump to a service here as the activity is dying
                            val spec = UnzipPassController.InputStreamUnzipControllerSpec(fromURI, application, passStore, null, null)
                            UnzipPassController.processInputStream(spec)
                        } else {
                            UnzipPassDialog.show(fromURI, this, passStore) { path ->
                                // TODO this is kind of a hack - there should be a better way
                                val id = path.split("/".toRegex()).dropLastWhile(String::isEmpty).toTypedArray().last()

                                val passbookForId = passStore.getPassbookForId(id)
                                passStore.currentPass = passbookForId

                                passStore.classifier.moveToTopic(passbookForId!!, getString(R.string.topic_new))

                                startActivityFromClass(PassViewActivity::class.java)
                                finish()
                            }
                        }
                    }
                })
            } catch (e: Exception) {
                if (e.message?.contains("Permission") == true && !withPermission) {
                    PassImportActivityPermissionsDispatcher.doImportWithCheck(this@PassImportActivity, true)
                } else {
                    tracker.trackException("Error in import", e, false)
                }

            }


        }).start()
    }


    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun showDeniedDialog() {
        progressDialog.dismissIfShowing()
        alert(R.string.error_no_permission_msg, R.string.error_no_permission_title, onOKListener = DialogInterface.OnClickListener { _, _ -> finish() })
    }

}
