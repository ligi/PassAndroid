package org.ligi.passandroid.ui

import android.app.Activity
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import org.ligi.axt.AXT
import org.ligi.passandroid.R
import org.ligi.passandroid.model.pass.BarCode

internal class BarcodeUIController(rootView: View, private val barCode: BarCode?, activity: Activity, private val passViewHelper: PassViewHelper) {

    val zoomOut: View by lazy { rootView.findViewById(R.id.zoomOut) }
    val zoomIn: View  by lazy { rootView.findViewById(R.id.zoomIn) }
    val barcode_img: ImageView  by lazy { rootView.findViewById(R.id.barcode_img) as ImageView }
    val barcodeAlternativeText: TextView by lazy { rootView.findViewById(R.id.barcode_alt_text) as TextView }

    private var currentBarcodeWidth: Int = 0

    init {

        zoomIn.setOnClickListener {
            setBarCodeSize(currentBarcodeWidth + passViewHelper.fingerSize)
        }

        zoomOut.setOnClickListener {
            setBarCodeSize(currentBarcodeWidth - passViewHelper.fingerSize)
        }

        if (barCode != null) {
            val smallestSide = AXT.at(activity.windowManager).smallestSide

            val bitmapDrawable = barCode.getBitmap(activity.resources)

            if (bitmapDrawable != null) {
                barcode_img.setImageDrawable(bitmapDrawable)
                barcode_img.visibility = VISIBLE
            } else {
                barcode_img.visibility = GONE
            }

            if (barCode.alternativeText != null) {
                barcodeAlternativeText.text = barCode.alternativeText
                barcodeAlternativeText.visibility = VISIBLE
            } else {
                barcodeAlternativeText.visibility = GONE
            }

            setBarCodeSize(smallestSide / 2)
        } else {
            passViewHelper.setBitmapSafe(barcode_img, null)
            zoomIn.visibility = GONE
            zoomOut.visibility = GONE
        }
    }


    fun setBarCodeSize(width: Int) {

        zoomOut.visibility = if (width < passViewHelper.fingerSize * 2) INVISIBLE else VISIBLE

        if (width > passViewHelper.windowWidth - passViewHelper.fingerSize * 2) {
            zoomIn.visibility = INVISIBLE
        } else {
            zoomIn.visibility = VISIBLE
        }

        currentBarcodeWidth = width
        val quadratic = barCode!!.format!!.isQuadratic
        barcode_img.layoutParams = LinearLayout.LayoutParams(width, if (quadratic) width else ViewGroup.LayoutParams.WRAP_CONTENT)
    }


}
