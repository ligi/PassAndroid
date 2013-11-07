package org.ligi.ticketviewer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.ligi.ticketviewer.model.PassbookParser;
import org.ligi.ticketviewer.ui.TicketViewActivityBase;
import org.ligi.tracedroid.logging.Log;

import java.util.List;

public class LocationsMapFragment extends SupportMapFragment {
    private GoogleMap mMap;
    private TicketViewActivityBase base_activity;
    public boolean click_to_fullscreen = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        base_activity = (TicketViewActivityBase) getActivity();

        if (!(getActivity() instanceof TicketViewActivityBase)) {
            throw new IllegalArgumentException("LocationsMapFragment must be used inside a TicketViewActivityBase");
        }

        mMap = getMap();


        root.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {


                        LatLngBounds.Builder boundser = new LatLngBounds.Builder();

                        List<PassbookParser.PassLocation> locations = base_activity.passbookParser.getLocations();


                        if (locations.size() > 0) {
                            for (PassbookParser.PassLocation l : locations) {
                                Log.i("adding marker" + l.latlng);

                                // yea that looks stupid but need to split LatLng free/nonfree - google play services ^^
                                LatLng latLng=new LatLng(l.latlng.lat,l.latlng.lon);
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(l.description)
                                        //.icon(BitmapDescriptorFactory.fromBitmap(base_activity.passbookParser.getIconBitmap())));
                                );

                                boundser = boundser.include(latLng);
                                Log.i("added marker");
                            }

                            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                @Override
                                public void onInfoWindowClick(Marker marker) {
                                    Intent i = new Intent();
                                    i.setAction(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse("geo:" + marker.getPosition().latitude + "," + marker.getPosition().longitude));
                                    getActivity().startActivity(i);
                                }
                            });
                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundser.build(), 100));
                            // Remove listener to prevent position reset on camera move.
                            mMap.setOnCameraChangeListener(null);
                            if (click_to_fullscreen) mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {
                                    Intent i = new Intent(getActivity(), FullscreenMapActivity.class);
                                    i.putExtra("path", base_activity.passbookParser.getPath());
                                    getActivity().startActivity(i);
                                }
                            });
                        }

                        // at this point, the UI is fully displayed
                    }
                });


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();


    }
}