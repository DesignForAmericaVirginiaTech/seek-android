package com.designforamerica.seek.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.designforamerica.seek.models.Location;
import com.designforamerica.seek.ParseHelper;
import com.designforamerica.seek.R;
import com.designforamerica.seek.Seek;
import com.designforamerica.seek.activities.LocationActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by jbruzek on 3/26/15.
 */
public class MapFragment extends Fragment {

    private MapView mapView;
    private GoogleMap map;
    private ParseHelper ph;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_fragment, container, false);
        container.removeAllViews();

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        //try {
        MapsInitializer.initialize(this.getActivity());
        //} catch (GooglePlayServicesNotAvailableException e) {
        //    e.printStackTrace();
        //}

        // Updates the location and zoom of the MapView
        Bundle b = getArguments();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(b.getDouble("lat"), b.getDouble("lon")), 14);
        map.moveCamera(cameraUpdate);

        ArrayList<Location> li = Seek.getLocations();
        for (Location l : li) {
            if (l.def()) {
                map.addMarker(new MarkerOptions().position(new LatLng(l.latitude(), l.longitude())).title(l.id()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_primary)));
            } else {
                map.addMarker(new MarkerOptions().position(new LatLng(l.latitude(), l.longitude())).title(l.id()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_accent)));
            }
        }
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent i = new Intent(getActivity(), LocationActivity.class);
                i.putExtra("id",marker.getTitle());
                startActivity(i);
                return true;
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
