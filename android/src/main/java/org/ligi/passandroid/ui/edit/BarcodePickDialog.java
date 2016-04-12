package org.ligi.passandroid.ui.edit;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import org.greenrobot.eventbus.EventBus;
import org.ligi.passandroid.R;
import org.ligi.passandroid.events.PassRefreshEvent;
import org.ligi.passandroid.model.pass.BarCode;
import org.ligi.passandroid.model.pass.Pass;

public class BarcodePickDialog {

    public static void show(final AppCompatActivity context, final EventBus bus, final Pass pass, final BarCode barCode) {
        final View view = LayoutInflater.from(context).inflate(R.layout.barcode_edit, null);

        final BarcodeEditController barcodeEditController = new BarcodeEditController(view, context, barCode);

        new AlertDialog.Builder(context).setView(view)
                                        .setTitle("Edit BarCode")
                                        .setNegativeButton(android.R.string.cancel, null)
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(final DialogInterface dialog, final int which) {
                                                pass.setBarCode(barcodeEditController.getBarCode());
                                                bus.post(new PassRefreshEvent(pass));
                                            }
                                        })
                                        .show();
    }

}
