package org.ligi.passandroid.ui.views

import android.content.Context
import android.support.v7.widget.AppCompatDrawableManager
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import org.ligi.passandroid.R

open class TimeAndNavBar constructor(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private fun baseTextView(context: Context) = TextView(context).apply {
        layoutParams = weightLayoutParams
        val dimension = context.resources.getDimension(R.dimen.rhythm).toInt()
        setPadding(dimension, dimension, dimension, dimension)
        isClickable = true
        setBackgroundResource(R.drawable.clickable_bg)
    }

    val navTextView by lazy {
        baseTextView(context)
    }


    val timeTextView by lazy {
        baseTextView(context).apply {
            gravity = Gravity.RIGHT
        }
    }

    val weightLayoutParams by lazy {
        LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            weight = 1.toFloat()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        addView(navTextView)
        addView(timeTextView)

        val drawableManager = AppCompatDrawableManager.get()
        val navDrawable = drawableManager.getDrawable(context, R.drawable.ic_maps_place)
        navTextView.setCompoundDrawablesWithIntrinsicBounds(navDrawable, null, null, null)
        val timeDrawable = drawableManager.getDrawable(context, R.drawable.ic_action_today)
        timeTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, timeDrawable, null)
    }

}
