package org.ligi.passandroid.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import org.ligi.axt.AXT;
import org.ligi.passandroid.R;
import org.ligi.passandroid.TicketDefinitions;
import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.maps.PassbookMapsFacade;
import org.ligi.passandroid.model.Passbook;

import java.io.File;

public class PassMenuOptions {
    public final Activity activity;
    public final Passbook passbook;

    public PassMenuOptions(Activity activity, Passbook passbook) {
        this.activity = activity;
        this.passbook = passbook;
    }

    public boolean process(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_delete:
                Tracker.get().trackEvent("ui_action", "delete", "delete", null);
                new AlertDialog.Builder(activity).setMessage("Do you really want to delete this passbook?").setTitle("Sure?")
                        .setPositiveButton(activity.getString(R.string.delete), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AXT.at(new File(passbook.getPath())).deleteRecursive();
                                if (activity instanceof TicketViewActivityBase) {
                                    Intent ticketListIntent = new Intent(activity, TicketListActivity.class);
                                    NavUtils.navigateUpTo(activity, ticketListIntent);
                                } else if (activity instanceof TicketListActivity) {
                                    ((TicketListActivity) activity).refreshPasses();
                                }
                            }

                        }).setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;

            case R.id.menu_map:
                PassbookMapsFacade.startFullscreenMap(activity, passbook);
                return true;

            case R.id.menu_share:
                Tracker.get().trackEvent("ui_action", "share", "shared", null);
                new PassExportTask(activity, passbook.getPath(), TicketDefinitions.getShareDir(activity), "share.pkpass", true).execute();
                return true;
        }
        return false;
    }


}
