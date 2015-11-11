package org.ligi.passandroid.ui.edit_fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.larswerkman.holocolorpicker.ColorPicker;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.events.PassRefreshEvent;
import org.ligi.passandroid.model.PassImpl;
import org.ligi.passandroid.model.PassStore;

import javax.inject.Inject;

public class ColorPickFragment extends PassStoreBackedFragment {

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
                App.getBus().post(new PassRefreshEvent(getPass()));
            }
        });

        colorPicker.setColor(getPass().getBackgroundColor());

        return view;
    }


}
