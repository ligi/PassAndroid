package org.ligi.passandroid.ui.views

import android.content.Context
import android.support.v7.widget.AppCompatDrawableManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.time_and_nav.view.*
import org.ligi.passandroid.R

class TimeAndNavBar constructor(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.time_and_nav, this)
        AppCompatDrawableManager.get().apply {
            val navDrawable = getDrawable(context, R.drawable.ic_maps_place)
            timeButton.setCompoundDrawablesWithIntrinsicBounds(navDrawable, null, null, null)

            val timeDrawable = getDrawable(context, R.drawable.ic_action_today)
            locationButton.setCompoundDrawablesWithIntrinsicBounds(null, null, timeDrawable, null)
        }
    }

}
