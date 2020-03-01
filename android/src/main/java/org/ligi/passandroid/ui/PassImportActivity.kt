package org.ligi.passandroid.ui

import android.Manifest
import android.os.Bundle
import android.view.View.GONE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_import.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.ligi.kaxt.startActivityFromClass
import org.ligi.kaxtui.alert
import org.ligi.passandroid.R
import org.ligi.passandroid.Tracker
import org.ligi.passandroid.functions.fromURI
import org.ligi.passandroid.model.PassStore
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions


@RuntimePermissions
class PassImportActivity : AppCompatActivity() {

    val tracker: Tracker by inject()
    val passStore: PassStore by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.data?.scheme == null) {
            tracker.trackException("invalid_import_uri", false)
            finish()
            return
        }

        setContentView(R.layout.activity_import)

        doImportWithPermissionCheck(false)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun doImport(withPermission: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val fromURI = fromURI(this@PassImportActivity, intent!!.data!!, tracker)

                withContext(Dispatchers.Main) {

                    progress_container.visibility = GONE

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
                            UnzipPassDialog.show(fromURI, this@PassImportActivity, passStore) { path ->
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
                }
            } catch (e: Exception) {
                if (e.message?.contains("Permission") == true && !withPermission) {
                    doImportWithPermissionCheck(true)
                } else {
                    tracker.trackException("Error in import", e, false)
                }
            }
        }
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun showDeniedDialog() {
        progress_container.visibility = GONE
        alert(R.string.error_no_permission_msg, R.string.error_no_permission_title, onOK = { finish() })
    }
}
