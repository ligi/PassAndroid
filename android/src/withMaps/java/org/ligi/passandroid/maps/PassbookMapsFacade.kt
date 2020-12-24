package org.ligi.passandroid.maps

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import org.ligi.kaxt.startActivityFromClass
import org.ligi.passandroid.FullscreenMapActivity
import org.ligi.passandroid.LocationsMapFragment
import org.ligi.passandroid.R

object PassbookMapsFacade {

    fun init(context: FragmentActivity): Boolean {
        val gapi = GoogleApiAvailability.getInstance()
        val isGooglePlayServicesAvailable = gapi.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS

        if (!isGooglePlayServicesAvailable) {
            return false
        }

        val locationsMapFragment = LocationsMapFragment()
        locationsMapFragment.clickToFullscreen = true
        context.supportFragmentManager.commit { replace<LocationsMapFragment>(R.id.map_container) }

        return true
    }

    fun startFullscreenMap(context: Context) {
        context.startActivityFromClass(FullscreenMapActivity::class.java)
    }
}