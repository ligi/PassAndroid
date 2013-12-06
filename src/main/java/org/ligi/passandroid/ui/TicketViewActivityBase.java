package org.ligi.passandroid.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;

import com.androidquery.AQuery;

import org.ligi.axt.AXT;
import org.ligi.passandroid.R;
import org.ligi.passandroid.TicketDefinitions;
import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.maps.PassbookMapsFacade;
import org.ligi.passandroid.model.Passbook;

import java.io.File;

public class TicketViewActivityBase extends ActionBarActivity {

    protected Bitmap icon_bitmap;
    protected String path;
    public Passbook passbook;
    private AQuery aQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        path = getIntent().getStringExtra("path");

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        passbook = new Passbook(path);

        loadIcon();
    }

    private void loadIcon() {
        Display display = getWindowManager().getDefaultDisplay();
        int smallestSide = Math.min(display.getHeight(), display.getWidth());
        int size = (int) (2.0f * smallestSide / 3.0f);

        icon_bitmap = passbook.getIconBitmap();

        if (icon_bitmap != null) {
            icon_bitmap = Bitmap.createScaledBitmap(icon_bitmap, size, size, true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_ticket_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(TicketViewActivityBase.this, TicketListActivity.class);

                startActivity(intent);
                break;

            case R.id.menu_delete:
                Tracker.get().trackEvent("ui_action", "delete", "delete", null);
                new AlertDialog.Builder(this).setMessage("Do you really want to delete this passbook?").setTitle("Sure?")
                        .setPositiveButton("Yes", new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AXT.at(new File(path)).deleteRecursive();
                                finish();
                            }

                        }).setNegativeButton("No", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                break;

            case R.id.menu_map:
                PassbookMapsFacade.startFullscreenMap(this, passbook);
                break;

            case R.id.menu_share:
                Tracker.get().trackEvent("ui_action", "share", "shared", null);
                new PassExportTask(this, passbook.getPath(), TicketDefinitions.getShareDir(this), "share.pkpass", true).execute();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        Tracker.get().activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Tracker.get().activityStop(this);
    }

}
