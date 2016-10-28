package org.ligi.passandroid.ui.pass_view_holder

import android.app.Activity
import android.support.v7.widget.CardView
import android.view.View
import kotlinx.android.synthetic.main.pass_list_item.view.*
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.pass.Pass

open class VerbosePassViewHolder(view: CardView) : PassViewHolder(view) {

    override fun apply(pass: Pass, passStore: PassStore, activity: Activity) {
        super.apply(pass, passStore, activity)

        var stringToSetForDateOrExtraText: String? = null

        val timeInfoString = getTimeInfoString(pass)
        if (timeInfoString != null) {
            stringToSetForDateOrExtraText = timeInfoString
        } else if (getExtraString(pass) != null) {
            stringToSetForDateOrExtraText = getExtraString(pass)
        }

        if (stringToSetForDateOrExtraText != null && !stringToSetForDateOrExtraText.isEmpty()) {
            view.date.text = stringToSetForDateOrExtraText
            view.date.visibility = View.VISIBLE
        } else {
            view.date.visibility = View.GONE
        }
    }
}
