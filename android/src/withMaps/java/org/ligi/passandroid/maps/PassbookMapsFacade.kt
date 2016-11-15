package org.ligi.passandroid.maps


import android.content.Context
import android.support.v4.app.FragmentActivity
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

        val ft = context.supportFragmentManager.beginTransaction()
        val locationsMapFragment = LocationsMapFragment()
        locationsMapFragment.click_to_fullscreen = true
        ft.replace(R.id.map_container, locationsMapFragment)
        ft.commit()

        return true
    }

    fun startFullscreenMap(context: Context) {
        context.startActivityFromClass(FullscreenMapActivity::class.java)
    }
}