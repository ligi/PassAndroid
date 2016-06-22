package org.ligi.passandroid.ui.edit;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import com.larswerkman.holocolorpicker.ColorPicker;
import org.greenrobot.eventbus.EventBus;
import org.ligi.passandroid.R;
import org.ligi.passandroid.events.PassRefreshEvent;
import org.ligi.passandroid.model.pass.Pass;

public class ColorPickDialog {

    public static void showColorDialog(final Context context, final Pass pass, final EventBus bus) {
        final View inflate = LayoutInflater.from(context).inflate(R.layout.edit_color, null);

        final ColorPicker colorPicker = (ColorPicker) inflate.findViewById(R.id.colorPicker);
        colorPicker.setColor(pass.getAccentColor());
        colorPicker.setOldCenterColor(pass.getAccentColor());
        new AlertDialog.Builder(context).setView(inflate).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                pass.setAccentColor(colorPicker.getColor());
                bus.post(new PassRefreshEvent(pass));
            }
        }).setNegativeButton(android.R.string.cancel, null).setTitle(R.string.change_color_dialog_title).show();
    }
}
