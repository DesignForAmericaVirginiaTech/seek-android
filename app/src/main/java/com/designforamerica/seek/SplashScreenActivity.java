package com.designforamerica.seek;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;


public class SplashScreenActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LoginDialog.LoginDialogListener, ParseCallbacks {

    protected GoogleApiClient mGoogleApiClient;
    protected android.location.Location mLastLocation;
    private ParseHelper ph;
    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePic;
    private String coverPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        FacebookSdk.sdkInitialize(getApplicationContext());

        buildGoogleApiClient();

        ph = new ParseHelper(this);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            getFacebookInfo(currentUser);
        } else {
            DialogFragment newFragment = new LoginDialog();
            newFragment.show(getFragmentManager(), "login");
        }

    }

    private void getFacebookInfo(ParseUser user) {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            Log.d("JSON", object.toString(4));

                            //String coverPhotoUrl = response.getJSONObject().getJSONObject("cover").getString("source");
                            name = object.getString("name");
                            firstName = object.getString("first_name");
                            lastName = object.getString("last_name");
                            email = object.getString("email");
                            profilePic = "https://graph.facebook.com/" + object.getString("id") + "/picture?type=large";
                            JSONObject cover = object.getJSONObject("cover");
                            coverPic = cover.getString("source");
                            Seek.setProfileInformation(name, firstName, lastName, email, profilePic, coverPic);
                            //callback to the main thread
                            complete(true);
                            ph.queryLocations();
                            ph.queryMyLocations(ParseUser.getCurrentUser().getObjectId());
                            ph.queryFavoriteLocations(ParseUser.getCurrentUser().getObjectId());
                            ph.queryDistances();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,picture,first_name,last_name,cover,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void launchMainActivity(final String name, final String fName, final String lName, final String email, final String pic, final String cover) {
        // This method will be executed once the timer is over
        // Start your app main activity
        Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
        if (mLastLocation != null) {
            i.putExtra("lon", mLastLocation.getLongitude());
            i.putExtra("lat", mLastLocation.getLatitude());
        } else {
            i.putExtra("lon", -80.4209);
            i.putExtra("lat", 37.22666);
        }
        startActivity(i);
        // close this activity
        finish();
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

            Log.i("Location", "" + mLastLocation.getLatitude());
            Log.i("Location", "" + mLastLocation.getLongitude());
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * if the user wants to login with Facebook.
     * @param l
     */
    @Override
    public void onDialogPositiveClick(LoginDialog l) {
        l.dismiss();
        //List<String> permissions = Arrays.asList("public_profile", "email", "user_photos", "cover");
        List<String> permissions = Arrays.asList("public_profile", "email", "user_photos");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                    Toast.makeText(getApplicationContext(), "Cancelled Login", Toast.LENGTH_SHORT).show();
                } else if (user.isNew()) {
                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                    Toast.makeText(getApplicationContext(), "New log in successful", Toast.LENGTH_SHORT).show();
                    getFacebookInfo(user);
                } else {
                    Log.d("MyApp", "User logged in through Facebook!");
                    Toast.makeText(getApplicationContext(), "Existing log in successful", Toast.LENGTH_SHORT).show();
                    getFacebookInfo(user);
                }
            }
        });
    }

    /**
     * if the user chooses to login anonymously.
     * @param l
     */
    @Override
    public void onDialogNegativeClick(LoginDialog l) {
        l.dismiss();

        //anon-i-moose
    }

    /**
     * Track the progress of the various callbacks
     * This screen depends on five asynchronous callbacks:
     *
     * Facebook login
     * Parse Location query
     * Parse Distance query
     * Parse favorite query
     * Parse myLocations query
     *
     * @param empty
     */
    @Override
    public void complete(boolean empty) {
        //if all the callbacks have returned
        if (Seek.complete()) {
            //process all the locations in Seek
            Seek.processLocations();
            //and launch
            launchMainActivity(name, firstName, lastName, email, profilePic, coverPic);
        }
    }
}
