package org.ligi.passandroid.ui.views

import android.content.Context
import androidx.appcompat.widget.AppCompatDrawableManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.time_and_nav.view.*
import org.ligi.passandroid.R

class TimeAndNavBar constructor(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.time_and_nav, this)
        AppCompatDrawableManager.get().apply {

            val timeDrawable = getDrawable(context, R.drawable.ic_action_today)
            timeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, timeDrawable, null)

            val navDrawable = getDrawable(context, R.drawable.ic_maps_place)
            locationButton.setCompoundDrawablesWithIntrinsicBounds(navDrawable, null, null, null)
        }
    }

}
