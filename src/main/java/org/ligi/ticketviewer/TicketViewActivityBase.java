package org.ligi.ticketviewer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.androidquery.AQuery;
import org.ligi.ticketviewer.helper.FileHelper;
import org.ligi.ticketviewer.maps.PassbookMapsFacade;

import java.io.File;

public class TicketViewActivityBase extends SherlockFragmentActivity {


    protected Bitmap icon_bitmap;
    protected String path;
    protected PassbookParser passbookParser;
    private AQuery aQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        path = getIntent().getStringExtra("path");

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Display display = getWindowManager().getDefaultDisplay();

        passbookParser = new PassbookParser(path,this);

        int size = (int) (2.0f * Math.min((int) display
                .getHeight(), (int) display.getWidth()) / 3.0f);

        icon_bitmap = passbookParser.getIconBitmap();

        if (icon_bitmap != null)
            icon_bitmap = Bitmap.createScaledBitmap(icon_bitmap, size, size, true);

        Log.i("", "loading " + path);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_ticket_view, menu);
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
                                FileHelper.DeleteRecursive(new File(path));
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
                PassbookMapsFacade.startFullscreenMap(this, passbookParser);
                break;

            case R.id.menu_share:
                Tracker.get().trackEvent("ui_action", "share", "shared", null);
                new PassExportTask(this, passbookParser.getPath(), TicketDefinitions.getShareDir(this), "share.pkpass", true).execute();
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

    public AQuery getAQ() {
        if (aQuery == null) {
            aQuery = new AQuery(this);
        }
        return aQuery;
    }
}
