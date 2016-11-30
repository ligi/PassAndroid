package org.ligi.passandroid.ui

import android.app.Activity
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.barcode.view.*
import org.ligi.kaxt.getSmallestSide
import org.ligi.passandroid.model.pass.BarCode

internal class BarcodeUIController(val rootView: View, private val barCode: BarCode?, activity: Activity, private val passViewHelper: PassViewHelper) {

    fun getBarcodeView() = rootView.barcode_img

    private var currentBarcodeWidth: Int = 0

    init {

        rootView.zoomIn.setOnClickListener {
            setBarCodeSize(currentBarcodeWidth + passViewHelper.fingerSize)
        }

        rootView.zoomOut.setOnClickListener {
            setBarCodeSize(currentBarcodeWidth - passViewHelper.fingerSize)
        }

        if (barCode != null) {
            val smallestSide = activity.windowManager.getSmallestSide()

            val bitmapDrawable = barCode.getBitmap(activity.resources)

            if (bitmapDrawable != null) {
                rootView.barcode_img.setImageDrawable(bitmapDrawable)
                rootView.barcode_img.visibility = VISIBLE
            } else {
                rootView.barcode_img.visibility = GONE
            }

            if (barCode.alternativeText != null) {
                rootView.barcode_alt_text.text = barCode.alternativeText
                rootView.barcode_alt_text.visibility = VISIBLE
            } else {
                rootView.barcode_alt_text.visibility = GONE
            }

            setBarCodeSize(smallestSide / 2)
        } else {
            passViewHelper.setBitmapSafe(rootView.barcode_img, null)
            rootView.zoomIn.visibility = GONE
            rootView.zoomOut.visibility = GONE
        }
    }


    private fun setBarCodeSize(width: Int) {

        rootView.zoomOut.visibility = if (width < passViewHelper.fingerSize * 2) INVISIBLE else VISIBLE

        if (width > passViewHelper.windowWidth - passViewHelper.fingerSize * 2) {
            rootView.zoomIn.visibility = INVISIBLE
        } else {
            rootView.zoomIn.visibility = VISIBLE
        }

        currentBarcodeWidth = width
        val quadratic = barCode!!.format!!.isQuadratic()
        rootView.barcode_img.layoutParams = LinearLayout.LayoutParams(width, if (quadratic) width else ViewGroup.LayoutParams.WRAP_CONTENT)
    }


}
