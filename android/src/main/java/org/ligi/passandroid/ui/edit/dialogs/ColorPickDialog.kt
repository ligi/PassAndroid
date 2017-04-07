package org.ligi.passandroid.ui.edit.dialogs

import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.edit_color.view.*
import org.greenrobot.eventbus.EventBus
import org.ligi.passandroid.R
import org.ligi.passandroid.events.PassRefreshEvent
import org.ligi.passandroid.model.pass.Pass

fun showColorPickDialog(context: Context, pass: Pass, bus: EventBus) {
    val inflate = LayoutInflater.from(context).inflate(R.layout.edit_color, null)

    inflate.colorPicker.color = pass.accentColor
    inflate.colorPicker.oldCenterColor = pass.accentColor
    AlertDialog.Builder(context).setView(inflate).setPositiveButton(android.R.string.ok) { _, _ ->
        pass.accentColor = inflate.colorPicker.color
        bus.post(PassRefreshEvent(pass))
    }.setNegativeButton(android.R.string.cancel, null).setTitle(R.string.change_color_dialog_title).show()
}
