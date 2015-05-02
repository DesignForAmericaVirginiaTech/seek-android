package com.designforamerica.seek;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.software.shell.fab.ActionButton;

/**
 * Created by jbruzek on 4/2/15.
 */
public class LocationActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, NavChoiceDialog.NavChoiceDialogListener {
    private Toolbar toolbar;
    private TextView title;
    private TextView distance;
    private Button delete;
    private MapView mapView;
    private GoogleMap map;
    private Location location;

    protected GoogleApiClient mGoogleApiClient;
    protected android.location.Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        buildGoogleApiClient();

        Intent intent = getIntent();
        toolbar = (Toolbar) findViewById(R.id.location_tool_bar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.inflateMenu(R.menu.location);

        mapView = (MapView) findViewById(R.id.location_map_view);
        mapView.onCreate(savedInstanceState);

        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        location = Seek.getLocation(intent.getStringExtra("id"));

        MapsInitializer.initialize(this);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.latitude(), location.longitude()), 15);
        map.moveCamera(cameraUpdate);

        map.addMarker(new MarkerOptions().position(new LatLng(location.latitude(), location.longitude())).title(location.name()));

        title = (TextView) findViewById(R.id.location_title);
        title.setText(location.name());
        distance = (TextView) findViewById(R.id.location_distance);

        ActionButton actionButton = (ActionButton) findViewById(R.id.action_button);
        actionButton.setButtonColor(getResources().getColor(R.color.accent));
        actionButton.setButtonColorPressed(getResources().getColor(R.color.accentDark));
        actionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_nav));
        actionButton.show();
        actionButton.bringToFront();
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new NavChoiceDialog();
                newFragment.show(getFragmentManager(), "login");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.location, menu);

        if (location.def()) {
            menu.removeItem(R.id.edit);
        }

        MenuItem fav = menu.findItem(R.id.favorite);
        //set the favorite icon
        if (location.favorite()) {
            Drawable d = fav.getIcon();
            d.setColorFilter( 0xffffff00, PorterDuff.Mode.MULTIPLY );
            fav.setIcon(d);
        } else {
            Drawable d = fav.getIcon();
            d.clearColorFilter();
            fav.setIcon(d);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
        }
        if (id == R.id.favorite) {
            if (location.favorite()) {
                Drawable d = item.getIcon();
                d.clearColorFilter();
                item.setIcon(d);
                Seek.removeFavorite(location.id());
            } else {
                Drawable d = item.getIcon();
                d.setColorFilter( 0xffffff00, PorterDuff.Mode.MULTIPLY );
                item.setIcon(d);
                Seek.addFavorite(location.id());
            }
            return true;
        }

        else if (id == R.id.edit) {
            Intent i = new Intent(this, EditLocationActivity.class);
            i.putExtra("id", location.id());
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            locationUpdated(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
        else {
            Toast.makeText(this, "No location detected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Location", "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("Location", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
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

    public void locationUpdated(double latitude, double longitude) {
        double d = distance(location.latitude(), location.longitude(), latitude, longitude);

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

    @Override
    public void onWalkClick(NavChoiceDialog l) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + location.latitude() + "," + location.longitude() + "&mode=w");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    @Override
    public void onBicycleClick(NavChoiceDialog l) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + location.latitude() + "," + location.longitude() + "&mode=b");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    @Override
    public void onDriveClick(NavChoiceDialog l) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + location.latitude() + "," + location.longitude() + "&mode=d");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    /**
     * a dialog asking the user to confirm a location deletion
     */
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
