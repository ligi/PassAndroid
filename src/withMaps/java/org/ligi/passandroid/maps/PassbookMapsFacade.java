package org.ligi.passandroid.maps;


import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.ligi.axt.AXT;
import org.ligi.passandroid.App;
import org.ligi.passandroid.FullscreenMapActivity;
import org.ligi.passandroid.LocationsMapFragment;
import org.ligi.passandroid.R;
import org.ligi.passandroid.model.Pass;

public class PassbookMapsFacade {

    public static boolean init(FragmentActivity context) {
        final boolean isGooglePlayServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;

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

    public static void startFullscreenMap(Context context, Pass pass) {
        App.getPassStore().setCurrentPass(pass);
        AXT.at(context).startCommonIntent().activityFromClass(FullscreenMapActivity.class);
    }
}