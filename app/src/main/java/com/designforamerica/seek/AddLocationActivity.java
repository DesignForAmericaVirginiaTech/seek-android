package com.designforamerica.seek;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Activity to edit the details of a location
 *
 * Created by jbruzek on 5/1/15.
 */
public class AddLocationActivity extends ActionBarActivity {

    private Button cancel;
    private Button save;
    private Toolbar toolbar;
    private MapView mapView;
    private GoogleMap map;
    private EditText input;
    private LatLng loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        Intent i = getIntent();
        loc = new LatLng(i.getDoubleExtra("lat", -80.420482), i.getDoubleExtra("lon", 37.221861));

        toolbar = (Toolbar) findViewById(R.id.add_location_tool_bar);
        toolbar.setTitle("Edit Location");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //initialize the map and add a click listener for dropping pins
        mapView = (MapView) findViewById(R.id.add_location_map_view);
        mapView.onCreate(savedInstanceState);
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);
        MapsInitializer.initialize(this);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(loc, 15);
        map.moveCamera(cameraUpdate);
        map.addMarker(new MarkerOptions().position(loc));
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                map.clear();
                loc = latLng;
                map.addMarker(new MarkerOptions().position(latLng));
            }
        });

        //show a dialog instructing users how to drop a pin
        DialogFragment dpd = new DropPinDialog();
        dpd.show(getFragmentManager(), "Drop Pin");

        input = (EditText) findViewById(R.id.add_location_name);
        input.setFocusableInTouchMode(true);

        save = (Button) findViewById(R.id.button_confirm_add_location);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location l = new Location(input.getText().toString(), loc.latitude, loc.longitude, false, "");
                Seek.addLocation(l);
                finish();
            }
        });
        cancel = (Button) findViewById(R.id.button_cancel_add_location);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
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

    /**
     * a dialog telling the user how to drop a pin
     */
    private class DropPinDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Select Location")
                    .setMessage("Touch the location you want on the map to drop a pin")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //nothing
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
