package org.ligi.passandroid.ui.pass_view_holder;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import butterknife.OnClick;
import org.ligi.passandroid.R;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.pass.Pass;
import org.ligi.passandroid.model.pass.PassImpl;
import org.ligi.passandroid.ui.Visibility;
import org.threeten.bp.ZonedDateTime;
import static android.view.View.VISIBLE;

public class EditViewHolder extends VerbosePassViewHolder implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private ZonedDateTime time;
    private final Context context;


    public EditViewHolder(final View view) {
        super(view);
        context = view.getContext();

    }

    @OnClick(R.id.addCalendar)
    void onCalendarClick() {
        new DatePickerDialog(context, this, time.getYear(), time.getMonth().getValue() - 1, time.getDayOfMonth()).show();
    }


    @OnClick(R.id.navigateTo)
    void onNavigateClick() {
        new AlertDialog.Builder(context).setMessage("Not yet available").setPositiveButton(android.R.string.ok, null).show();
    }

    @Override
    public void apply(final Pass pass, final PassStore passStore, final Activity activity) {
        super.apply(pass, passStore, activity);

        if (pass.getCalendarTimespan() != null) {
            time = pass.getCalendarTimespan().getFrom();
        } else {
            time = ZonedDateTime.now();
        }
    }

    @Visibility
    @Override
    protected int getVisibilityForGlobalAndLocal(final boolean global, final boolean local) {
        return VISIBLE;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        time = time.withYear(year).withMonth(monthOfYear + 1).withDayOfMonth(dayOfMonth);

        pass.setCalendarTimespan(new PassImpl.TimeSpan(time, null, null));

        new TimePickerDialog(context, this, time.getHour(), time.getMinute(), true).show();
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        time = time.withHour(hourOfDay).withMinute(minute);

        pass.setCalendarTimespan(new PassImpl.TimeSpan(time, null, null));

        apply(pass, passStore, activity);
    }
}
