package com.designforamerica.seek;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.software.shell.fab.ActionButton;

/**
 * Created by jbruzek on 4/3/15.
 */
public class LocationFragment extends Fragment {

    private TextView title;
    private ScrollView scrollView;
    private MapView mapView;
    private GoogleMap map;
    private String name;
    private Double lat;
    private Double lon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.location_fragment, container, false);
        container.removeAllViews();

        mapView = (MapView) v.findViewById(R.id.location_map_view);
        mapView.onCreate(savedInstanceState);

        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        Bundle b = getArguments();
        name = b.getString("title");
        lat = b.getDouble("lat");
        lon = b.getDouble("lon");

        MapsInitializer.initialize(this.getActivity());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 15);
        map.moveCamera(cameraUpdate);

        map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(name));

        title = (TextView) v.findViewById(R.id.location_title);
        title.setText(name);

        ActionButton actionButton = (ActionButton) v.findViewById(R.id.action_button);
        actionButton.setButtonColor(getResources().getColor(R.color.accent));
        actionButton.setButtonColorPressed(getResources().getColor(R.color.accentDark));
        actionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_nav));
        actionButton.show();

        //Hide the toolbar when the user scrolls!
        scrollView = (ScrollView) v.findViewById(R.id.scroll_view);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int dy = scrollView.getScrollY();
                if (dy >= 300) {
                    ((ActionBarActivity)getActivity()).getSupportActionBar().hide();
                } else if (dy < 300) {
                    ((ActionBarActivity)getActivity()).getSupportActionBar().show();
                }
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
