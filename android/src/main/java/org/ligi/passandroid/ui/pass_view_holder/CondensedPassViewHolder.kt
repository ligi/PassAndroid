package org.ligi.passandroid.ui.pass_view_holder

import android.app.Activity
import android.support.v7.widget.CardView
import android.view.View
import kotlinx.android.synthetic.main.pass_list_item.view.*
import kotlinx.android.synthetic.main.time_and_nav.view.*
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.pass.Pass

class CondensedPassViewHolder(view: CardView) : PassViewHolder(view) {

    override fun apply(pass: Pass, passStore: PassStore, activity: Activity) {
        super.apply(pass, passStore, activity)

        val extraString = getExtraString(pass)

        if (extraString.isNullOrBlank()) {
            view.date.visibility = View.GONE
        } else {
            view.date.text = extraString
            view.date.visibility = View.VISIBLE
        }

        view.timeAndNavBar.timeButton.text = getTimeInfoString(pass)

    }
}
