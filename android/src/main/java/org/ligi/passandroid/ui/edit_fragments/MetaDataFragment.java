package org.ligi.passandroid.ui.edit_fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import org.joda.time.DateTime;
import org.ligi.axt.simplifications.SimpleTextWatcher;
import org.ligi.passandroid.R;
import org.ligi.passandroid.events.PassRefreshEvent;
import org.ligi.passandroid.model.PassImpl;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MetaDataFragment extends PassandroidFragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    @Bind(R.id.descriptionEdit)
    EditText descriptionEdit;

    private DateTime time;
    private final PassImpl pass;

    public MetaDataFragment() {
        pass = (PassImpl) passStore.getCurrentPass();

        if (pass.getRelevantDate() != null) {
            time = pass.getRelevantDate();
        } else {
            time = DateTime.now();
        }
    }

    @OnClick(R.id.pickTime)
    public void onPickTime() {
        new TimePickerDialog(getActivity(), this, time.hourOfDay().get(), time.minuteOfHour().get(), true).show();
    }


    @OnClick(R.id.pickDate)
    public void onPickDate() {
        new DatePickerDialog(getActivity(), this, time.year().get(), time.monthOfYear().get() - 1, time.dayOfMonth().get()).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View inflate = inflater.inflate(R.layout.edit_meta_data, container, false);
        ButterKnife.bind(this, inflate);

        descriptionEdit.setText(pass.getDescription());
        descriptionEdit.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                pass.setDescription(s.toString());
                refresh();
                super.afterTextChanged(s);
            }
        });

        return inflate;
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        time = time.withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth);

        pass.setRelevantDate(time);

        refresh();
    }

    private void refresh() {
        bus.post(new PassRefreshEvent(pass));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        time = time.withHourOfDay(hourOfDay).withMinuteOfHour(minute);

        pass.setRelevantDate(time);
        refresh();
    }

}
