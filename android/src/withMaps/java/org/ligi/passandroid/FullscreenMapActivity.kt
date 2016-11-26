package org.ligi.passandroid

import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import org.ligi.passandroid.ui.PassViewActivityBase
import org.ligi.passandroid.ui.showNavigateToLocationsDialog

class FullscreenMapActivity : PassViewActivityBase() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ConnectionResult.SUCCESS != GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
            fallbackForMissingGooglePlay()
        }

        setContentView(R.layout.fullscreen_map)
    }

    private fun fallbackForMissingGooglePlay() {
        if (currentPass != null) {
            showNavigateToLocationsDialog(currentPass, true)
        }
    }

}
