package org.ligi.passandroid.ui

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import org.ligi.kaxt.getSizeAsPointCompat
import org.ligi.passandroid.R

class PassViewHelper(private val context: Activity) {

    val fingerSize by lazy { context.resources.getDimensionPixelSize(R.dimen.finger) }

    fun setBitmapSafe(imageView: ImageView, bitmap: Bitmap?) {

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
            imageView.visibility = View.VISIBLE
            imageView.layoutParams = getLayoutParamsSoThatWeHaveMinimumAFingerInHeight(imageView, bitmap)
        } else {
            imageView.visibility = View.GONE
        }
    }

    fun getLayoutParamsSoThatWeHaveMinimumAFingerInHeight(imageView: ImageView, bitmap: Bitmap)
            = imageView.layoutParams!!.apply {
        height = if (bitmap.height < fingerSize) {
            fingerSize
        } else {
            LinearLayout.LayoutParams.WRAP_CONTENT
        }
    }

    val windowWidth by lazy { context.windowManager.getSizeAsPointCompat().x }

}
