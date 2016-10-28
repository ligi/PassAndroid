package org.ligi.passandroid.ui.views

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import kotlinx.android.synthetic.main.category_indicator.view.*
import org.ligi.passandroid.R

class CategoryIndicatorViewWithIcon(context: Context, attrs: AttributeSet) : BaseCategoryIndicatorView(context, attrs, R.layout.category_indicator) {

    fun setIcon(iconBitmap: Bitmap) {
        iconImageView.setImageBitmap(iconBitmap)
    }

}
