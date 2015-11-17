package org.ligi.passandroid.ui.edit_fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.larswerkman.holocolorpicker.ColorPicker;

import org.ligi.passandroid.R;
import org.ligi.passandroid.events.PassRefreshEvent;

public class ColorPickFragment extends PassandroidFragment {

    @Bind(R.id.colorPicker)
    ColorPicker colorPicker;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.edit_color, container, false);
        ButterKnife.bind(this, view);

        colorPicker.setOldCenterColor(getPass().getBackgroundColor());

        // until PR is merged
        colorPicker.setShowOldCenterColor(false);

        colorPicker.setOnColorSelectedListener(new ColorPicker.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int i) {
                getPass().setBackgroundColor(i);
                bus.post(new PassRefreshEvent(getPass()));
            }
        });

        colorPicker.setColor(getPass().getBackgroundColor());

        return view;
    }


}
