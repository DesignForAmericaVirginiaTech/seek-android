package com.designforamerica.seek;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.software.shell.fab.ActionButton;

/**
 * Created by jbruzek on 4/2/15.
 */
public class LocationActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private Toolbar toolbar;
    private LocationFragment locFrag;
    private LocationUpdateCallbacks luc;

    private boolean favorite = false;
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

        if (getFragmentManager().findFragmentById(R.layout.location_fragment) == null) {
            locFrag = new LocationFragment();
            luc = locFrag;
        }
        Bundle b = new Bundle();
        b.putString("title", intent.getStringExtra("title"));
        b.putDouble("lat", intent.getDoubleExtra("lat", 0));
        b.putDouble("lon", intent.getDoubleExtra("lon", 0));
        locFrag.setArguments(b);
        getFragmentManager().beginTransaction().add(R.id.location_container, locFrag).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
        }
        if (id == R.id.favorite) {
            if (favorite) {
                Drawable d = item.getIcon();
                d.clearColorFilter();
                item.setIcon(d);
                favorite = false;
            } else {
                Drawable d = item.getIcon();
                d.setColorFilter( 0xffffff00, PorterDuff.Mode.MULTIPLY );
                item.setIcon(d);
                favorite = true;
            }
            return true;
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
            luc.locationUpdated(mLastLocation.getLatitude(), mLastLocation.getLongitude());
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
}
