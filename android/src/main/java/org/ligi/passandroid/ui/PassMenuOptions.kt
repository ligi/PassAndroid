package org.ligi.passandroid.ui

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NavUtils
import kotlinx.android.synthetic.main.delete_dialog_layout.view.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ligi.kaxt.startActivityFromClass
import org.ligi.passandroid.R
import org.ligi.passandroid.Tracker
import org.ligi.passandroid.maps.PassbookMapsFacade
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.Settings
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.printing.doPrint
import java.io.File

class PassMenuOptions(val activity: Activity, val pass: Pass) : KoinComponent {

    val passStore: PassStore by inject()
    val tracker: Tracker by inject()
    val settings: Settings by inject()

    fun process(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.menu_delete -> {
                tracker.trackEvent("ui_action", "delete", "delete", null)

                val builder = AlertDialog.Builder(activity)
                builder.setMessage(activity.getString(R.string.dialog_delete_confirm_text))
                builder.setTitle(activity.getString(R.string.dialog_delete_title))
                builder.setIcon(R.drawable.ic_alert_warning)

                val sourceDeleteCheckBoxView = LayoutInflater.from(activity).inflate(R.layout.delete_dialog_layout, null)

                val source = pass.getSource(passStore)
                if (source != null && source.startsWith("file://")) {

                    sourceDeleteCheckBoxView.sourceDeleteCheckbox.text = activity.getString(R.string.dialog_delete_confirm_delete_source_checkbox)
                    builder.setView(sourceDeleteCheckBoxView)
                }

                builder.setPositiveButton(activity.getString(R.string.delete)) { _, _ ->
                    if (sourceDeleteCheckBoxView.sourceDeleteCheckbox.isChecked) {

                        File(source!!.replace("file://", "")).delete()
                    }
                    passStore.deletePassWithId(pass.id)
                    if (activity is PassViewActivityBase) {
                        val passListIntent = Intent(activity, PassListActivity::class.java)
                        NavUtils.navigateUpTo(activity, passListIntent)
                    }
                }
                builder.setNegativeButton(android.R.string.no, null)

                builder.show()

                return true
            }

            R.id.menu_map -> {
                PassbookMapsFacade.startFullscreenMap(activity)
                return true
            }

            R.id.menu_share -> {
                tracker.trackEvent("ui_action", "share", "shared", null)
                PassExportTaskAndShare(activity, passStore.getPathForID(pass.id)).execute()
                return true
            }

            R.id.menu_edit -> {
                tracker.trackEvent("ui_action", "share", "shared", null)
                passStore.currentPass = pass
                activity.startActivityFromClass(PassEditActivity::class.java)
                return true
            }

            R.id.menu_print -> {
                doPrint(activity, pass)
                return true
            }
        }
        return false
    }

}
