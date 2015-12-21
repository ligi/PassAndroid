package org.ligi.passandroid.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.widget.CheckBox;

import org.ligi.axt.AXT;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.maps.PassbookMapsFacade;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.Settings;

import java.io.File;

import javax.inject.Inject;

public class PassMenuOptions {

    @Inject
    PassStore passStore;

    @Inject
    Tracker tracker;

    @Inject
    Settings settings;

    public final Activity activity;
    public final Pass pass;

    public PassMenuOptions(final Activity activity, final Pass pass) {
        App.component().inject(this);
        this.activity = activity;
        this.pass = pass;
    }

    public boolean process(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_delete:
                tracker.trackEvent("ui_action", "delete", "delete", null);

                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(activity.getString(R.string.dialog_delete_confirm_text));
                builder.setTitle(activity.getString(org.ligi.passandroid.R.string.dialog_delete_title));
                builder.setIcon(R.drawable.ic_warning_amber_36dp);

                final CheckBox sourceDeleteCheckBox = new CheckBox(activity);

                if (pass.getSource() != null && pass.getSource().startsWith("file://")) {
                    sourceDeleteCheckBox.setText(activity.getString(R.string.dialog_delete_confirm_delete_source_checkbox));
                    builder.setView(sourceDeleteCheckBox);
                }

                builder.setPositiveButton(activity.getString(R.string.delete), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (sourceDeleteCheckBox.isChecked()) {
                            new File(pass.getSource().replace("file://", "")).delete();
                        }
                        passStore.deletePassWithId(pass.getId());
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
                tracker.trackEvent("ui_action", "share", "shared", null);
                new AlertDialog.Builder(activity).setItems(new CharSequence[]{"OpenPass ( no Apple support yet)", "Passbook"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                exportInFormat(which);
                            }
                        }).show();

                return true;

            case R.id.menu_edit:
                tracker.trackEvent("ui_action", "share", "shared", null);
                passStore.setCurrentPass(pass);
                AXT.at(activity).startCommonIntent().activityFromClass(PassEditActivity.class);
                return true;
        }
        return false;
    }

    private void exportInFormat(final int which) {
        final int passFormat = getPassFormat(which);
        new PassExportTask(activity, pass.getPath(), settings.getShareDir(), "share", true, passFormat).execute();
    }

    @PassExporter.PassFormat
    private int getPassFormat(int which) {
        switch (which) {
            case 1:
                return PassExporter.FORMAT_PKPASS;

            default:
                return PassExporter.FORMAT_OPENPASS;
        }
    }


}
