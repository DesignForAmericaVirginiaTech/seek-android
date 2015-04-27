package com.designforamerica.seek;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
public class LocationFragment extends Fragment implements LocationUpdateCallbacks {

    private TextView title;
    private TextView distance;
    private Button delete;
    private MapView mapView;
    private GoogleMap map;
    private String name;
    private Double lat;
    private Double lon;
    private Boolean def;

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
        def = b.getBoolean("def");

        MapsInitializer.initialize(this.getActivity());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 15);
        map.moveCamera(cameraUpdate);

        map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(name));

        title = (TextView) v.findViewById(R.id.location_title);
        title.setText(name);
        distance = (TextView) v.findViewById(R.id.location_distance);
        delete = (Button) v.findViewById(R.id.delete_location_button);
        if (def) {
            ((LinearLayout)delete.getParent()).removeView(delete);
        } else {
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteDialog dd = new DeleteDialog();
                    dd.show(getFragmentManager(), "delete");
                }
            });
        }

        ActionButton actionButton = (ActionButton) v.findViewById(R.id.action_button);
        actionButton.setButtonColor(getResources().getColor(R.color.accent));
        actionButton.setButtonColorPressed(getResources().getColor(R.color.accentDark));
        actionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_nav));
        actionButton.show();
        actionButton.bringToFront();

        //Hide the toolbar when the user scrolls!
//        scrollView = (ScrollView) v.findViewById(R.id.scroll_view);
//        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//            @Override
//            public void onScrollChanged() {
//                int dy = scrollView.getScrollY();
//                if (dy >= 300) {
//                    ((ActionBarActivity)getActivity()).getSupportActionBar().hide();
//                } else if (dy < 300) {
//                    ((ActionBarActivity)getActivity()).getSupportActionBar().show();
//                }
//            }
//        });

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


    @Override
    public void locationUpdated(double latitude, double longitude) {
        double d = distance(lat, lon, latitude, longitude);

        distance.setText(d + " miles away");
    }

    /**
     * Get the distance between two latlng points.
     * Taken from here:
     * http://www.geodatasource.com/developers/java
     *
     * Slightly edited so it rounds to one decimal place
     */
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 10;
        long tmp = Math.round(dist);
        return (double) tmp / 10;
    }

    /**
     * used by distance()
     */
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /**
     * used by distance()
     */
    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    private class DeleteDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Delete Location")
            .setMessage("This action cannot be undone")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(getActivity(), "CONFIRM", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(getActivity(), "cancel", Toast.LENGTH_SHORT).show();
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
