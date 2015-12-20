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

import org.ligi.axt.simplifications.SimpleTextWatcher;
import org.ligi.passandroid.R;
import org.ligi.passandroid.events.PassRefreshEvent;
import org.ligi.passandroid.model.PassImpl;
import org.threeten.bp.ZonedDateTime;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MetaDataFragment extends PassandroidFragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    @Bind(R.id.descriptionEdit)
    EditText descriptionEdit;

    private ZonedDateTime time;
    private final PassImpl pass;

    public MetaDataFragment() {
        pass = (PassImpl) passStore.getCurrentPass();

        if (pass.getCalendarTimespan() != null) {
            time = pass.getCalendarTimespan().getFrom();
        } else {
            time = ZonedDateTime.now();
        }
    }

    @OnClick(R.id.pickTime)
    public void onPickTime() {
        new TimePickerDialog(getActivity(), this, time.getHour(), time.getMinute(), true).show();
    }


    @OnClick(R.id.pickDate)
    public void onPickDate() {
        new DatePickerDialog(getActivity(), this, time.getYear(), time.getMonth().getValue() - 1, time.getDayOfMonth()).show();
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

        time = time.withYear(year).withMonth(monthOfYear + 1).withDayOfMonth(dayOfMonth);

        pass.setCalendarTimespan(new PassImpl.TimeSpan(time,null,null));

        refresh();
    }

    private void refresh() {
        bus.post(new PassRefreshEvent(pass));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        time = time.withHour(hourOfDay).withMinute(minute);

        pass.setCalendarTimespan(new PassImpl.TimeSpan(time,null,null));

        refresh();
    }

}
