package org.ligi.ticketviewer;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.ligi.ticketviewer.model.PassbookParser;
import org.ligi.ticketviewer.ui.TicketViewActivityBase;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class FullscreenMapActivity extends TicketViewActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (ConnectionResult.SUCCESS != GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) { // no google play services
            final List<PassbookParser.PassLocation> locations = passbookParser.getLocations();

            if (locations.size() == 0) {
                finish();
            } else if (locations.size() == 1) {
                startIntentForLocation(locations.get(0));
                finish();
            } else if (locations.size() > 1) {
                String[] cs = new String[locations.size()];

                int i = 0;
                for (PassbookParser.PassLocation loc : locations) {
                    cs[i++] = loc.description;
                }
                new AlertDialog.Builder(this).setTitle("Choose Location").setItems(cs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startIntentForLocation(locations.get(which));
                        finish();
                    }
                }).show();

            }
            return;

        }


        setContentView(R.layout.fullscreen_map);

    }

    private void startIntentForLocation(PassbookParser.PassLocation location) {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_VIEW);

        String description = "";
        try {
            description = URLEncoder.encode(location.description, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // OK - no descripion
        }

        PassbookParser.PassLocation.LatLng latlng = location.latlng;
        String latAndLonStr = latlng.lat + "," + latlng.lon;
        i.setData(Uri.parse("geo:" + latAndLonStr + "?q=" + latAndLonStr + "(" + description + ")"));
        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {

            i.setData(Uri.parse("http://maps.google.com/?q=" + description + "@" + latlng.lat + "," + latlng.lon));
            startActivity(i);
            // TODO also the browser could not be found -> handle
        }
    }
}
