package org.ligi.passandroid.ui.pass_view_holder

import android.app.Activity
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.View.*
import kotlinx.android.synthetic.main.pass_list_item.view.*
import org.ligi.passandroid.R
import org.ligi.passandroid.actions.AddToCalendar
import org.ligi.passandroid.model.PassBitmapDefinitions
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.model.pass.PassField
import org.ligi.passandroid.ui.NavigateToLocationsDialog
import org.ligi.passandroid.ui.Visibility
import org.threeten.bp.ZonedDateTime

abstract class PassViewHolder(val view: CardView) : RecyclerView.ViewHolder(view) {

    open fun apply(pass: Pass, passStore: PassStore, activity: Activity) {
        setupButtons(activity, pass)

        refresh(pass, passStore)
    }

    open fun setupButtons(activity: Activity, pass: Pass) {
        view.timeAndNavBar.timeTextView.text = view.context.getString(R.string.pass_to_calendar)
        view.timeAndNavBar.navTextView.text = view.context.getString(R.string.pass_directions)

        view.timeAndNavBar.timeTextView.setOnClickListener {
            val dateOrExtraText = getDateOrExtraText(pass)
            AddToCalendar.tryAddDateToCalendar(pass, view, dateOrExtraText)
        }

        view.timeAndNavBar.navTextView.setOnClickListener {
            NavigateToLocationsDialog.perform(activity, pass, false)
        }
    }

    protected fun refresh(pass: Pass, passStore: PassStore) {
        val dateOrExtraText = getDateOrExtraText(pass)

        val noButtons = dateOrExtraText == null && pass.locations.size <= 0

        view.actionsSeparator.visibility = getVisibilityForGlobalAndLocal(noButtons, true)
        view.timeAndNavBar.navTextView.visibility = getVisibilityForGlobalAndLocal(noButtons, pass.locations.size > 0)

        view.timeAndNavBar.timeTextView.visibility = getVisibilityForGlobalAndLocal(noButtons, dateOrExtraText != null)

        val iconBitmap = pass.getBitmap(passStore, PassBitmapDefinitions.BITMAP_ICON)

        iconBitmap?.let { view.categoryView.setIcon(it) }

        view.categoryView.setImageByCategory(pass.type)

        view.categoryView.setAccentColor(pass.accentColor)

        view.passTitle.text = pass.description
    }

    fun getExtraString(pass: Pass) = pass.fields.firstOrNull()?.let { getExtraStringForField(it) }


    private fun getExtraStringForField(passField: PassField): String {
        val stringBuilder = StringBuilder()

        if (passField.label != null) {
            stringBuilder.append(passField.label)

            if (passField.value != null) {
                stringBuilder.append(": ")
            }
        }

        if (passField.value != null) {
            stringBuilder.append(passField.value)
        }

        return stringBuilder.toString()
    }

    private fun setDateTextFromDateAndPrefix(prefix: String, relevantDate: ZonedDateTime): String {
        val relativeDateTimeString = DateUtils.getRelativeDateTimeString(view.context,
                relevantDate.toEpochSecond() * 1000,
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS,
                0)

        return prefix + relativeDateTimeString
    }

    protected fun getTimeInfoString(pass: Pass) =
            if (pass.calendarTimespan != null && pass.calendarTimespan!!.from != null) {
                setDateTextFromDateAndPrefix("", pass.calendarTimespan!!.from!!)
            } else if (pass.validTimespans != null && pass.validTimespans!!.size > 0 && pass.validTimespans!![0].to != null) {
                val to = pass.validTimespans!![0].to
                setDateTextFromDateAndPrefix(if (to!!.isAfter(ZonedDateTime.now())) "expires " else " expired ", to)
            } else {
                null
            }


    private fun getDateOrExtraText(pass: Pass): ZonedDateTime? {
        if (pass.calendarTimespan != null && pass.calendarTimespan!!.from != null) {
            return pass.calendarTimespan!!.from
        }
        if (pass.validTimespans != null && pass.validTimespans!!.size > 0) {
            return pass.validTimespans!![0].to
        }
        return null
    }

    @Visibility
    protected open fun getVisibilityForGlobalAndLocal(global: Boolean, local: Boolean): Int {
        if (global) {
            return GONE
        }

        return if (local) VISIBLE else INVISIBLE
    }
}
