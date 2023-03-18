package org.ligi.passandroid.ui.pass_view_holder

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import android.view.View.VISIBLE
import android.widget.DatePicker
import android.widget.TimePicker
import kotlinx.android.synthetic.main.edit.view.*
import kotlinx.android.synthetic.main.time_and_nav.view.*
import org.ligi.passandroid.R
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.model.pass.PassImpl
import org.ligi.passandroid.ui.Visibility
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit

class EditViewHolder(view: CardView) : VerbosePassViewHolder(view), TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private lateinit var time: ZonedDateTime
    private lateinit var pass: PassImpl
    private lateinit var passStore: PassStore

    override fun apply(pass: Pass, passStore: PassStore, activity: Activity) {
        super.apply(pass, passStore, activity)

        this.pass = pass as PassImpl
        this.passStore = passStore

        val calendarTimespan = pass.calendarTimespan
        time = if (calendarTimespan?.from != null) {
            calendarTimespan.from
        } else {
            val roundedTime = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES)
            roundedTime.plusMinutes(30L - (roundedTime.minute % 30))
        }
    }

    override fun setupButtons(activity: Activity, pass: Pass) {

        view.timeAndNavBar.timeButton.text = view.context.getString(R.string.edit_time)
        view.timeAndNavBar.locationButton.text = view.context.getString(R.string.edit_location)

        view.timeAndNavBar.timeButton.setOnClickListener {
            DatePickerDialog(view.context, this, time.year, time.month.value - 1, time.dayOfMonth).show()
        }

        view.timeAndNavBar.locationButton.setOnClickListener {
            AlertDialog.Builder(view.context).setMessage("Not yet available").setPositiveButton(android.R.string.ok, null).show()
        }

    }

    @Visibility
    override fun getVisibilityForGlobalAndLocal(global: Boolean, local: Boolean): Int {
        return VISIBLE
    }

    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {

        time = time.withYear(year).withMonth(monthOfYear + 1).withDayOfMonth(dayOfMonth)

        pass.calendarTimespan = PassImpl.TimeSpan(time, null, null)

        TimePickerDialog(itemView.context, this, time.hour, time.minute, true).show()
    }


    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {

        time = time.withHour(hourOfDay).withMinute(minute)

        pass.calendarTimespan = PassImpl.TimeSpan(time, null, null)

        refresh(pass, passStore)
    }
}
