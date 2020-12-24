package org.ligi.passandroid

import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import org.ligi.passandroid.ui.PassViewActivityBase
import org.ligi.passandroid.ui.showNavigateToLocationsDialog

class FullscreenMapActivity : PassViewActivityBase() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiAvailability = GoogleApiAvailability.getInstance()
        if (ConnectionResult.SUCCESS != apiAvailability.isGooglePlayServicesAvailable(this)) {
            fallbackForMissingGooglePlay()
        }

        setContentView(R.layout.fullscreen_map)
    }

    private fun fallbackForMissingGooglePlay() {
        showNavigateToLocationsDialog(currentPass, true)
    }

}
