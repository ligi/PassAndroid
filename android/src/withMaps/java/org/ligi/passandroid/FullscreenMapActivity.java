package org.ligi.passandroid;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import org.ligi.passandroid.ui.NavigateToLocationsDialog;
import org.ligi.passandroid.ui.PassViewActivityBase;

public class FullscreenMapActivity extends PassViewActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ConnectionResult.SUCCESS != GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
            fallbackForMissingGooglePlay();
        }

        setContentView(R.layout.fullscreen_map);
    }

    private void fallbackForMissingGooglePlay() {
        if (currentPass != null) {
            NavigateToLocationsDialog.perform(this, currentPass, true);
        }
    }

}
