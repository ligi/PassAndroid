package org.ligi.passandroid

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import org.ligi.kaxt.startActivityFromClass
import org.ligi.passandroid.ui.PassViewActivityBase

class LocationsMapFragment : SupportMapFragment() {

    private var base_activity: PassViewActivityBase? = null
    var click_to_fullscreen = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)

        base_activity = activity as PassViewActivityBase

        if (activity !is PassViewActivityBase) {
            throw IllegalArgumentException("LocationsMapFragment must be used inside a PassViewActivityBase")
        }

        getMapAsync { map ->
            map.setOnMapLoadedCallback {
                if (click_to_fullscreen)
                    map.setOnMapClickListener {
                        App.passStore.currentPass = base_activity!!.currentPass
                        activity.startActivityFromClass(FullscreenMapActivity::class.java)
                    }


                var boundBuilder = LatLngBounds.Builder()

                val locations = base_activity!!.currentPass.locations

                for (l in locations) {

                    // yea that looks stupid but need to split LatLng free/nonfree - google play services ^^
                    val latLng = LatLng(l.lat, l.lon)
                    val marker = MarkerOptions().position(latLng).title(l.getNameWithFallback(base_activity!!.currentPass))
                    map.addMarker(marker)


                    boundBuilder = boundBuilder.include(latLng)
                }

                map.setOnInfoWindowClickListener { marker ->
                    val i = Intent()
                    i.action = Intent.ACTION_VIEW
                    i.data = Uri.parse("geo:" + marker.position.latitude + "," + marker.position.longitude + "?q=" + marker.title)
                    activity.startActivity(i)
                }
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundBuilder.build(), 100))

                // limit zoom-level to 17 - otherwise we could be so zoomed in that it looks buggy
                map.moveCamera(CameraUpdateFactory.zoomTo(Math.min(17f, map.cameraPosition.zoom)))
            }
        }

        return root
    }
}