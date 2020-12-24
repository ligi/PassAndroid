package org.ligi.passandroid

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.android.ext.android.inject
import org.ligi.kaxt.startActivityFromClass
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.ui.PassViewActivityBase
import kotlin.math.min

class LocationsMapFragment : SupportMapFragment() {
    var clickToFullscreen = false

    val passStore : PassStore by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)
        val baseActivity = activity as PassViewActivityBase

        getMapAsync { map ->
            map.setOnMapLoadedCallback {
                if (clickToFullscreen)
                    map.setOnMapClickListener {
                        passStore.currentPass = baseActivity.currentPass
                        baseActivity.startActivityFromClass(FullscreenMapActivity::class.java)
                    }


                var boundBuilder = LatLngBounds.Builder()

                val locations = baseActivity.currentPass.locations

                for (l in locations) {

                    // yea that looks stupid but need to split LatLng free/nonfree - google play services ^^
                    val latLng = LatLng(l.lat, l.lon)
                    val marker = MarkerOptions().position(latLng).title(l.getNameWithFallback(baseActivity.currentPass))
                    map.addMarker(marker)


                    boundBuilder = boundBuilder.include(latLng)
                }

                map.setOnInfoWindowClickListener { marker ->
                    val i = Intent()
                    i.action = Intent.ACTION_VIEW
                    i.data = ("geo:" + marker.position.latitude + "," + marker.position.longitude + "?q=" + marker.title).toUri()
                    baseActivity.startActivity(i)
                }
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundBuilder.build(), 100))

                // limit zoom-level to 17 - otherwise we could be so zoomed in that it looks buggy
                map.moveCamera(CameraUpdateFactory.zoomTo(min(17f, map.cameraPosition.zoom)))
            }
        }

        return root
    }
}