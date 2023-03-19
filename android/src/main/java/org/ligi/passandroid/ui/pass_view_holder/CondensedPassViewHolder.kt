package org.ligi.passandroid.ui.pass_view_holder

import android.app.Activity
import androidx.cardview.widget.CardView
import android.view.View
import android.widget.TextView
import org.ligi.passandroid.R
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.pass.Pass

class CondensedPassViewHolder(view: CardView) : PassViewHolder(view) {

    override fun apply(pass: Pass, passStore: PassStore, activity: Activity) {
        super.apply(pass, passStore, activity)

        val extraString = getExtraString(pass)

        val date = view.findViewById<TextView>(R.id.date)


        if (extraString.isNullOrBlank()) {
            date.visibility = View.GONE
        } else {
            date.text = extraString
            date.visibility = View.VISIBLE
        }

        view.findViewById<TextView>(R.id.timeButton).text = getTimeInfoString(pass)

    }
}
