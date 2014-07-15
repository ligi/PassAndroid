package org.ligi.passandroid;

import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.ligi.passandroid.ui.NavigateToLocationsDialog;
import org.ligi.passandroid.ui.TicketViewActivityBase;

public class FullscreenMapActivity extends TicketViewActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ConnectionResult.SUCCESS != GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) { // no google play services
            NavigateToLocationsDialog.perform(this, optionalPass.get(), true); // fallback
        }

        setContentView(R.layout.fullscreen_map);
    }


}
