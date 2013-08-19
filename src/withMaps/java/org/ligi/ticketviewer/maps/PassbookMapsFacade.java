package org.ligi.ticketviewer.maps;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.ligi.ticketviewer.FullscreenMapActivity;
import org.ligi.ticketviewer.LocationsMapFragment;
import org.ligi.ticketviewer.PassbookParser;
import org.ligi.ticketviewer.R;

public class PassbookMapsFacade {

    public static boolean init(FragmentActivity context) {
        boolean isGooglePlayServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;

        if (!isGooglePlayServicesAvailable) {
            return false;
        }


        FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
        LocationsMapFragment locationsMapFragment = new LocationsMapFragment();
        locationsMapFragment.click_to_fullscreen = true;
        ft.replace(R.id.map_container, locationsMapFragment);
        ft.commit();


        return true;
    }

    public static void startFullscreenMap(Context context, PassbookParser passbookParser) {
        Intent i = new Intent(context, FullscreenMapActivity.class);
        i.putExtra("path", passbookParser.getPath());
        context.startActivity(i);
    }
}