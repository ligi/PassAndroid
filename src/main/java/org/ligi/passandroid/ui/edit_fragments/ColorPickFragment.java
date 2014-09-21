package org.ligi.passandroid.ui.edit_fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.larswerkman.holocolorpicker.ColorPicker;

import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.events.PassRefreshEvent;
import org.ligi.passandroid.model.PassImpl;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ColorPickFragment extends Fragment {

    @InjectView(R.id.colorPicker)
    ColorPicker colorPicker;

    private final PassImpl pass;

    public ColorPickFragment() {
        pass = (PassImpl) App.getPassStore().getCurrentPass().get();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.edit_color, null);
        ButterKnife.inject(this, view);

        colorPicker.setOldCenterColor(pass.getBackGroundColor());

        // until PR is merged
        colorPicker.setShowOldCenterColor(false);

        colorPicker.setOnColorSelectedListener(new ColorPicker.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int i) {
                pass.setBackgroundColor(i);
                App.getBus().post(new PassRefreshEvent(pass));
            }
        });

        colorPicker.setColor(pass.getBackGroundColor());

        return view;
    }


}
