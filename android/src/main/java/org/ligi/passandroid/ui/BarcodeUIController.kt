package org.ligi.passandroid.ui

import android.app.Activity
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import org.ligi.kaxt.getSmallestSide
import org.ligi.passandroid.R
import org.ligi.passandroid.model.pass.BarCode

internal class BarcodeUIController(private val rootView: View, private val barCode: BarCode?, activity: Activity, private val passViewHelper: PassViewHelper) {

    fun getBarcodeView(): ImageView = rootView.findViewById(R.id.barcode_img)

    private var currentBarcodeWidth: Int = 0

    private val zoomIn = rootView.findViewById<AppCompatImageView>(R.id.zoomIn)
    private val zoomOut = rootView.findViewById<AppCompatImageView>(R.id.zoomOut)
    private val barcodeImage = rootView.findViewById<ImageView>(R.id.barcode_img)
    private val barcodeAltText = rootView.findViewById<TextView>(R.id.barcode_alt_text)

    init {

        zoomIn.setOnClickListener {
            setBarCodeSize(currentBarcodeWidth + passViewHelper.fingerSize)
        }

        zoomOut.setOnClickListener {
            setBarCodeSize(currentBarcodeWidth - passViewHelper.fingerSize)
        }

        if (barCode != null) {
            val smallestSide = activity.windowManager.getSmallestSide()

            val bitmapDrawable = barCode.getBitmap(activity.resources)

            if (bitmapDrawable != null) {
                barcodeImage.setImageDrawable(bitmapDrawable)
                barcodeImage.visibility = VISIBLE
            } else {
                barcodeImage.visibility = GONE
            }

            if (barCode.alternativeText != null) {
                barcodeAltText.text = barCode.alternativeText
                barcodeAltText.visibility = VISIBLE
            } else {
                barcodeAltText.visibility = GONE
            }

            setBarCodeSize(smallestSide / 2)
        } else {
            passViewHelper.setBitmapSafe(barcodeImage, null)
            zoomIn.visibility = GONE
            zoomOut.visibility = GONE
        }
    }


    private fun setBarCodeSize(width: Int) {

        zoomOut.visibility = if (width < passViewHelper.fingerSize * 2) INVISIBLE else VISIBLE

        if (width > passViewHelper.windowWidth - passViewHelper.fingerSize * 2) {
            zoomIn.visibility = INVISIBLE
        } else {
            zoomIn.visibility = VISIBLE
        }

        currentBarcodeWidth = width
        val quadratic = barCode!!.format!!.isQuadratic()
        barcodeImage.layoutParams = FrameLayout.LayoutParams(width, if (quadratic) width else ViewGroup.LayoutParams.WRAP_CONTENT)
    }


}
