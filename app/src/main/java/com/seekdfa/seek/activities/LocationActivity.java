package com.seekdfa.seek.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.seekdfa.seek.models.Location;
import com.seekdfa.seek.dialogs.NavChoiceDialog;
import com.seekdfa.seek.R;
import com.seekdfa.seek.Seek;
import com.seekdfa.seek.utilities.Distances;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.software.shell.fab.ActionButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by jbruzek on 4/2/15.
 */
public class LocationActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, NavChoiceDialog.NavChoiceDialogListener {
    private Toolbar toolbar;
    private TextView title;
    private TextView distance;
    private MapView mapView;
    private GoogleMap map;
    private Location location;
    //TextViews that show the times
    private TextView walk;
    private TextView bike;
    private TextView drive;
    private TextView bus;
    //loading spinners
    private ProgressBar wBar;
    private ProgressBar bBar;
    private ProgressBar dBar;
    private ProgressBar buBar;

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

        if (location.def()) {
            map.addMarker(new MarkerOptions().position(new LatLng(location.latitude(), location.longitude())).title(location.name()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_primary)));
        } else {
            map.addMarker(new MarkerOptions().position(new LatLng(location.latitude(), location.longitude())).title(location.name()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_accent)));
        }

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

        walk = (TextView) findViewById(R.id.walk_time);
        walk.setVisibility(View.INVISIBLE);
        bike = (TextView) findViewById(R.id.bike_time);
        bike.setVisibility(View.INVISIBLE);
        drive = (TextView) findViewById(R.id.drive_time);
        drive.setVisibility(View.INVISIBLE);
        bus = (TextView) findViewById(R.id.bus_time);
        bus.setVisibility(View.INVISIBLE);
        wBar = (ProgressBar) findViewById(R.id.walk_spinner);
        bBar = (ProgressBar) findViewById(R.id.bike_spinner);
        dBar = (ProgressBar) findViewById(R.id.drive_spinner);
        buBar = (ProgressBar) findViewById(R.id.bus_spinner);
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
            finish();
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
            //SEND ALL THE HTTP REQUESTS!!!!!!!
            String origin = mLastLocation.getLatitude() + "," + mLastLocation.getLongitude();
            String destination = location.latitude() + "," + location.longitude();
            new HttpTimeTask().execute("https://maps.googleapis.com/maps/api/directions/json?origin=" + origin + "&destination=" + destination + "&mode=walking&key=" + getResources().getString(R.string.server_key), "walk");
            new HttpTimeTask().execute("https://maps.googleapis.com/maps/api/directions/json?origin=" + origin + "&destination=" + destination + "&mode=bicycling&key=" + getResources().getString(R.string.server_key), "bike");
            new HttpTimeTask().execute("https://maps.googleapis.com/maps/api/directions/json?origin=" + origin + "&destination=" + destination + "&mode=driving&key=" + getResources().getString(R.string.server_key), "drive");
            new HttpTimeTask().execute("https://maps.googleapis.com/maps/api/directions/json?origin=" + origin + "&destination=" + destination + "&transit_mode=bus&key=" + getResources().getString(R.string.server_key), "bus");
            locationUpdated(mLastLocation);
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

    /**
     * Update the location, this updates the current location in Seek as well as updating the distance displayed on the screen
     * @param lloc
     */
    public void locationUpdated(android.location.Location lloc) {
        Seek.setLastLocation(lloc);
        double d = Distances.distance(location.latitude(), location.longitude(), lloc.getLatitude(), lloc.getLongitude());

        distance.setText(d + " miles away");
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
     * An AsyncTask that handle sending a GoogleDirections HTTP request, parsing the
     * JSON result, and setting the values of the directions.
     */
    private class HttpTimeTask extends AsyncTask<String, Void, String> {

        private String type;

        @Override
        protected String doInBackground(String... params) {
            type = params[1];
            return getJSON(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject json = null;
            try {
                json = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //set the correct textView
            switch(type) {
                case "walk":
                    //remove the spinner and set the text
                    ((LinearLayout) wBar.getParent()).removeView(wBar);
                    walk.setVisibility(View.VISIBLE);
                    walk.setText(getTime(json));
                    break;
                case "bike":
                    //remove the spinner and set the text
                    ((LinearLayout) bBar.getParent()).removeView(bBar);
                    bike.setVisibility(View.VISIBLE);
                    bike.setText(getTime(json));
                    break;
                case "drive":
                    //remove the spinner and set the text
                    ((LinearLayout) dBar.getParent()).removeView(dBar);
                    drive.setVisibility(View.VISIBLE);
                    drive.setText(getTime(json));
                    break;
                case "bus":
                    //remove the spinner and set the text
                    ((LinearLayout) buBar.getParent()).removeView(buBar);
                    bus.setVisibility(View.VISIBLE);
                    bus.setText(getTime(json));
                    break;
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        private String getJSON(String address){
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(address);
            try{
                HttpResponse response = client.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if(statusCode == 200){
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while((line = reader.readLine()) != null){
                        builder.append(line);
                    }
                } else {
                    Log.e(MainActivity.class.toString(),"Failed to get JSON object");
                }
            }catch(ClientProtocolException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
            return builder.toString();
        }

        private String getTime(JSONObject json) {
            int seconds = 0;
            try {
                JSONArray routes = json.getJSONArray("routes");
                JSONObject route0 = routes.getJSONObject(0);
                JSONArray legs = route0.getJSONArray("legs");
                JSONObject leg0 = legs.getJSONObject(0);
                JSONObject duration = leg0.getJSONObject("duration");
                seconds = duration.getInt("value");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return getMins(seconds);
        }

        private String getMins(int seconds) {
            if (type.equals("drive") && seconds > 120) {
                seconds += 240;
            }

            int minutes = seconds/60;

            return (minutes + " min");
        }
    }
}
