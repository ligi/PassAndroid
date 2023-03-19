package org.ligi.passandroid.ui.edit.dialogs

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import org.ligi.passandroid.R
import org.ligi.passandroid.databinding.EditColorBinding
import org.ligi.passandroid.model.pass.Pass

fun showColorPickDialog(context: Context, pass: Pass, refreshCallback : () -> Unit) {
    val inflate = EditColorBinding.inflate(LayoutInflater.from(context))

    inflate.colorPicker.color = pass.accentColor
    inflate.colorPicker.oldCenterColor = pass.accentColor
    AlertDialog.Builder(context).setView(inflate.root).setPositiveButton(android.R.string.ok) { _, _ ->
        pass.accentColor = inflate.colorPicker.color
        refreshCallback.invoke()
    }.setNegativeButton(android.R.string.cancel, null).setTitle(R.string.change_color_dialog_title).show()
}
