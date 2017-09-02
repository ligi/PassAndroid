package org.ligi.passandroid.ui.edit.dialogs

import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import org.greenrobot.eventbus.EventBus
import org.ligi.kaxt.inflate
import org.ligi.passandroid.R
import org.ligi.passandroid.events.PassRefreshEvent
import org.ligi.passandroid.model.pass.BarCode
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.ui.edit.BarcodeEditController

fun showBarcodeEditDialog(context: AppCompatActivity, bus: EventBus, pass: Pass, barCode: BarCode) {
    val view = context.inflate(R.layout.barcode_edit)

    val barcodeEditController = BarcodeEditController(view, context, barCode)

    AlertDialog.Builder(context).setView(view)
            .setTitle(R.string.edit_barcode_dialog_title)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                pass.barCode = barcodeEditController.getBarCode()
                bus.post(PassRefreshEvent(pass))
            }
            .show()
}
