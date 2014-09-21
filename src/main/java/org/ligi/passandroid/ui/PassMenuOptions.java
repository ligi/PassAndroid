package org.ligi.passandroid.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.CheckBox;

import org.ligi.axt.AXT;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.maps.PassbookMapsFacade;
import org.ligi.passandroid.model.Pass;

import java.io.File;

public class PassMenuOptions {
    public final Activity activity;
    public final Pass pass;

    public PassMenuOptions(final Activity activity, final Pass pass) {
        this.activity = activity;
        this.pass = pass;
    }

    public boolean process(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_delete:
                Tracker.get().trackEvent("ui_action", "delete", "delete", null);

                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(activity.getString(R.string.dialog_delete_confirm_text));
                builder.setTitle(activity.getString(org.ligi.passandroid.R.string.dialog_delete_title));
                builder.setIcon(R.drawable.ic_warning_amber_36dp);

                final CheckBox sourceDeleteCheckBox = new CheckBox(activity);
                if (pass.getSource().isPresent() && pass.getSource().get().startsWith("file://")) {
                    sourceDeleteCheckBox.setText(activity.getString(R.string.dialog_delete_confirm_delete_source_checkbox));
                    builder.setView(sourceDeleteCheckBox);
                }

                builder.setPositiveButton(activity.getString(R.string.delete), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (sourceDeleteCheckBox.isChecked()) {
                            new File(pass.getSource().get().replace("file://", "")).delete();
                        }
                        App.getPassStore().deletePassWithId(pass.getId());
                        if (activity instanceof PassViewActivityBase) {
                            final Intent passListIntent = new Intent(activity, PassListActivity.class);
                            NavUtils.navigateUpTo(activity, passListIntent);
                        } else if (activity instanceof PassListActivity) {
                            ((PassListActivity) activity).refreshPasses();
                        }
                    }

                });
                builder.setNegativeButton(android.R.string.no, null);

                builder.show();

                return true;

            case R.id.menu_map:
                PassbookMapsFacade.startFullscreenMap(activity, pass);
                return true;

            case R.id.menu_share:
                Tracker.get().trackEvent("ui_action", "share", "shared", null);
                new PassExportTask(activity, pass.getPath(), App.getShareDir(), "share.pkpass", true).execute();
                return true;

            case R.id.menu_edit:
                Tracker.get().trackEvent("ui_action", "share", "shared", null);
                App.getPassStore().setCurrentPass(pass);
                AXT.at(activity).startCommonIntent().activityFromClass(PassEditActivity.class);
                return true;
        }
        return false;
    }


}
