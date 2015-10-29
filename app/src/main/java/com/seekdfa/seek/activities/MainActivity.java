package com.seekdfa.seek.activities;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.seekdfa.seek.fragments.LocationsFragment;
import com.seekdfa.seek.fragments.MapFragment;
import com.seekdfa.seek.adapters.NavAdapter;
import com.seekdfa.seek.interfaces.NavDrawerCallbacks;
import com.seekdfa.seek.fragments.ProfileFragment;
import com.seekdfa.seek.R;
import com.seekdfa.seek.Seek;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.seekdfa.seek.utilities.Profile;

import java.text.DateFormat;
import java.util.Date;


/**
 * Main Activity for Seek
 *
 * Code for Location updates taken from Google Location Updates Sample
 * https://github.com/googlesamples/android-play-location/tree/master/LocationUpdates
 */
public class MainActivity extends ActionBarActivity implements NavDrawerCallbacks, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    /*
        Location services items
     */
    protected static final String TAG = "location-updates-sample";
    //Update time for location services. Inexact.
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    //Update limit. Exact.
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    protected LocationRequest mLocationRequest;
    protected GoogleApiClient mGoogleApiClient;
    protected android.location.Location mCurrentLocation;
    protected String mLastUpdateTime;

    /*
        Nav Drawer Items
     */
    //icons
    int ICONS[] = {
            R.drawable.ic_map,
            R.drawable.ic_profile,
            R.drawable.ic_locations
    };

    /*
        Other
     */
    private Toolbar toolbar;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    DrawerLayout Drawer;
    ActionBarDrawerToggle mDrawerToggle;
    MapFragment mapFrag;
    LocationsFragment locFrag;
    ProfileFragment proFrag;

    // Profile login information
    private String name;
    private String email;
    private String url;
    private String cover;

    /**
     * Initialize the toolbar and the nav drawer.
     * add the home fragment to the frame
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = Profile.getName();
        email = Profile.getEmail();
        url = Profile.getProfilePic();
        cover = Profile.getCoverPic();

        /**
         * Set up location updates
         */
        mLastUpdateTime = "";
        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);
        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleApiClient();

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new NavAdapter(
                getResources().getStringArray(R.array.nav_drawer_items),
                ICONS,
                name,
                email,
                url,
                cover,
                this,
                this);
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
        Drawer.setStatusBarBackgroundColor(getResources().getColor(R.color.primaryDark));
        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.open_drawer,R.string.close_drawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //drawer is open
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //drawer is closed
            }

        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();

        if (getFragmentManager().findFragmentById(R.layout.map_fragment) == null) {
            mapFrag = new MapFragment();
        }
        Bundle b = new Bundle();
        //get the location from the splash screen
        Intent intent = getIntent();
        b.putDouble("lon", intent.getDoubleExtra("lon", -80.4209));
        b.putDouble("lat", intent.getDoubleExtra("lat", 37.22666));
        b.putString("name", "Home");
        mapFrag.setArguments(b);
        getFragmentManager().beginTransaction().add(R.id.container, mapFrag).commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

//        SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView =
//                (SearchView) menu.findItem(R.id.search).getActionView();
//        searchView.setIconifiedByDefault(false);
//        ComponentName cn = new ComponentName(this, SearchResultsActivity.class);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            Intent i = new Intent(this, SettingsActivity.class);
//            startActivity(i);
//            return true;
//        }
        if (id == R.id.about) {
            Intent i = new Intent(this, AboutActivity.class);
            startActivity(i);
            return true;
        }
//        if (id == R.id.search) {
//            SearchView searchView = (SearchView) item.getActionView();
//            searchView.requestFocus();
//            searchView.setIconified(false);
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called by the Nav Drawer when an item in the nav drawer has been selected.
     * Swap the fragment in the frame, close the drawer, etc..
     * @param position
     */
    @Override
    public void itemSelected(int position) {
        Drawer.closeDrawers();
        Bundle b;
        switch(position) {
            case 1:
                toolbar.setTitle("Home");
                if (getFragmentManager().findFragmentById(R.layout.map_fragment) == null) {
                    mapFrag = new MapFragment();
                }
                b = new Bundle();
                b.putString("name", "Home");
                b.putDouble("lon", mCurrentLocation.getLongitude());
                b.putDouble("lat", mCurrentLocation.getLatitude());
                mapFrag.setArguments(b);
                getFragmentManager().beginTransaction().replace(R.id.container, mapFrag).commit();
                break;
            case 2:
                toolbar.setTitle("Profile");
                if (getFragmentManager().findFragmentById(R.layout.profile_fragment) == null) {
                    proFrag = new ProfileFragment();
                }
                b = new Bundle();
                b.putDouble("lat", mCurrentLocation.getLatitude());
                b.putDouble("lon", mCurrentLocation.getLongitude());
                proFrag.setArguments(b);
                getFragmentManager().beginTransaction().replace(R.id.container, proFrag).commit();
                break;
            case 3:
                toolbar.setTitle("Locations");
                if (getFragmentManager().findFragmentById(R.layout.locations_fragment) == null) {
                    locFrag = new LocationsFragment();
                }
                b = new Bundle();
                b.putString("name", "Locations");
                locFrag.setArguments(b);
                getFragmentManager().beginTransaction().replace(R.id.container, locFrag).commit();
                break;
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Seek.setLastLocation(mCurrentLocation);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            //updateUI();
        }

        startLocationUpdates();
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(android.location.Location location) {
        mCurrentLocation = location;
        Seek.setLastLocation(location);
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        //updateUI();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
            //updateUI();
        }
    }
}
