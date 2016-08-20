package org.ligi.passandroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;
import org.ligi.axt.AXT;
import org.ligi.passandroid.model.pass.PassLocation;
import org.ligi.passandroid.ui.PassViewActivityBase;

public class LocationsMapFragment extends SupportMapFragment {

    private PassViewActivityBase base_activity;
    public boolean click_to_fullscreen = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = super.onCreateView(inflater, container, savedInstanceState);

        base_activity = (PassViewActivityBase) getActivity();

        if (!(getActivity() instanceof PassViewActivityBase)) {
            throw new IllegalArgumentException("LocationsMapFragment must be used inside a PassViewActivityBase");
        }

        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap map) {

                map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        if (click_to_fullscreen) map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(final LatLng latLng) {
                                App.component().passStore().setCurrentPass(base_activity.currentPass);
                                AXT.at(getActivity()).startCommonIntent().activityFromClass(FullscreenMapActivity.class);
                            }
                        });


                        LatLngBounds.Builder boundBuilder = new LatLngBounds.Builder();

                        final List<PassLocation> locations = base_activity.currentPass.getLocations();

                        for (PassLocation l : locations) {

                            // yea that looks stupid but need to split LatLng free/nonfree - google play services ^^
                            final LatLng latLng = new LatLng(l.lat, l.lon);
                            map.addMarker(new MarkerOptions().position(latLng).title(l.getName(base_activity.currentPass))
                                          //.icon(BitmapDescriptorFactory.fromBitmap(base_activity.passbook.getIconBitmap())));
                            );

                            boundBuilder = boundBuilder.include(latLng);
                        }

                        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                final Intent i = new Intent();
                                i.setAction(Intent.ACTION_VIEW);
                                i.setData(Uri.parse("geo:" + marker.getPosition().latitude + "," + marker.getPosition().longitude + "?q=" + marker.getTitle()));
                                getActivity().startActivity(i);
                            }
                        });
                        map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundBuilder.build(), 100));

                        // limit zoom-level to 17 - otherwise we could be so zoomed in that it looks buggy
                        map.moveCamera(CameraUpdateFactory.zoomTo(Math.min(17f, map.getCameraPosition().zoom)));

                    }
                });

            }
        });

        return root;
    }
}