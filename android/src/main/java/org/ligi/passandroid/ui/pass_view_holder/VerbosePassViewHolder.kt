package org.ligi.passandroid.ui.pass_view_holder

import android.app.Activity
import androidx.cardview.widget.CardView
import android.view.View
import android.widget.TextView
import org.ligi.passandroid.R
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.pass.Pass

open class VerbosePassViewHolder(view: CardView) : PassViewHolder(view) {

    override fun apply(pass: Pass, passStore: PassStore, activity: Activity) {
        super.apply(pass, passStore, activity)

        val dateOrExtraText = getTimeInfoString(pass) ?: getExtraString(pass)

        val date = view.findViewById<TextView>(R.id.date)
        if (dateOrExtraText != null && dateOrExtraText.isNotEmpty()) {
            date.text = dateOrExtraText
            date.visibility = View.VISIBLE
        } else {
            date.visibility = View.GONE
        }
    }
}
