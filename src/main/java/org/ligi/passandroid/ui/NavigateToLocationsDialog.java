package org.ligi.passandroid.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import android.support.v7.app.AlertDialog;
import org.ligi.passandroid.R;
import org.ligi.passandroid.model.PassLocation;
import org.ligi.passandroid.model.Pass;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class NavigateToLocationsDialog {

    public static void perform(final Activity activity, Pass pass, final boolean finishOnDone) {
        final List<PassLocation> locations = pass.getLocations();

        if (locations.size() == 0) {
            done(activity, finishOnDone);
        } else if (locations.size() == 1) {
            startIntentForLocation(activity, locations.get(0));
            done(activity, finishOnDone);
        } else if (locations.size() > 1) {
            String[] locationDescriptions = new String[locations.size()];

            int i = 0;
            for (PassLocation loc : locations) {
                locationDescriptions[i++] = loc.getDescription();
            }
            new AlertDialog.Builder(activity).setTitle(activity.getString(R.string.choose_location)).setItems(locationDescriptions, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    startIntentForLocation(activity, locations.get(which));
                    done(activity, finishOnDone);
                }
            }).show();

        }
        return;

    }

    private static void done(Activity activity, boolean finishOnDone) {
        if (finishOnDone) {
            activity.finish();
        }
    }

    private static void startIntentForLocation(Activity activity, PassLocation location) {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_VIEW);

        String description = "";
        try {
            description = URLEncoder.encode(location.getDescription(), "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // OK - no descripion
        }

        PassLocation.LatLng latlng = location.latlng;
        String latAndLonStr = latlng.lat + "," + latlng.lon;
        i.setData(Uri.parse("geo:" + latAndLonStr + "?q=" + latAndLonStr + "(" + description + ")"));
        try {
            activity.startActivity(i);
        } catch (ActivityNotFoundException e) {
            i.setData(Uri.parse("http://maps.google.com/?q=" + description + "@" + latlng.lat + "," + latlng.lon));
            activity.startActivity(i);
            // TODO also the browser could not be found -> handle
        }
    }

}
