package com.designforamerica.seek;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
public class EditLocationActivity extends ActionBarActivity implements DeleteDialog.DeleteDialogListener {

    private Button cancel;
    private Button save;
    private Button delete;
    private Toolbar toolbar;
    private Location location;
    private MapView mapView;
    private GoogleMap map;
    private EditText input;
    private LatLng tempLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location);

        Intent intent = getIntent();
        location = Seek.getLocation(intent.getStringExtra("id"));
        tempLocation = new LatLng(location.latitude(), location.longitude());

        toolbar = (Toolbar) findViewById(R.id.edit_location_tool_bar);
        toolbar.setTitle("Edit Location");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //initialize the map and add a click listener for dropping pins
        mapView = (MapView) findViewById(R.id.edit_location_map_view);
        mapView.onCreate(savedInstanceState);
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);
        MapsInitializer.initialize(this);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.latitude(), location.longitude()), 15);
        map.moveCamera(cameraUpdate);
        map.addMarker(new MarkerOptions().position(new LatLng(location.latitude(), location.longitude())).title(location.name()));
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                map.clear();
                tempLocation = latLng;
                map.addMarker(new MarkerOptions().position(latLng));
            }
        });

        //show a dialog instructing users how to drop a pin
        DialogFragment dpd = new DropPinDialog();
        dpd.show(getFragmentManager(), "Drop Pin");

        input = (EditText) findViewById(R.id.edit_location_name);
        input.setHint(location.name());
        input.setFocusableInTouchMode(true);

        save = (Button) findViewById(R.id.button_confirm_edit_location);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName;
                if (input.getText().toString().equals("")) {
                    newName = location.name();
                } else {
                    newName = input.getText().toString();
                }
                Seek.updateLocation(location, newName, tempLocation);
                finish();
            }
        });
        cancel = (Button) findViewById(R.id.button_cancel_edit_location);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        delete = (Button) findViewById(R.id.button_delete_location);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dd = new DeleteDialog();
                dd.show(getFragmentManager(), "Delete");
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

    @Override
    public void deleted() {
        Seek.deleteLocation(location);
        finish();
    }
}
