package org.ligi.passandroid.functions

import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.CalendarContract
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.View
import org.ligi.passandroid.R
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.model.pass.PassImpl

fun tryAddDateToCalendar(pass: Pass, contextView: View, timeSpan: PassImpl.TimeSpan) {
    if (pass.calendarTimespan == null) {
        AlertDialog.Builder(contextView.context).setMessage(R.string.expiration_date_to_calendar_warning_message)
                .setTitle(R.string.expiration_date_to_calendar_warning_title)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { dialog, which -> reallyAddToCalendar(pass, contextView, timeSpan) }
                .show()
    } else {
        reallyAddToCalendar(pass, contextView, timeSpan)
    }
}

private fun reallyAddToCalendar(pass: Pass, contextView: View, date: PassImpl.TimeSpan) = try {
    val intent = Intent(Intent.ACTION_EDIT)
    intent.type = "vnd.android.cursor.item/event"
    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, date.from!!.toEpochSecond() * 1000)
    val to = date.to ?: date.from.plusHours(4)
    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, to.toEpochSecond() * 1000)
    intent.putExtra("title", pass.description)
    contextView.context.startActivity(intent)
} catch (exception: ActivityNotFoundException) {
    // TODO maybe action to install calendar app
    Snackbar.make(contextView, R.string.no_calendar_app_found, Snackbar.LENGTH_LONG).show()
}
