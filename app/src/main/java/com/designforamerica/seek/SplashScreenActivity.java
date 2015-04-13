package com.designforamerica.seek;

import android.app.Activity;
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


public class SplashScreenActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static int SPLASH_TIME_OUT = 500;
    protected GoogleApiClient mGoogleApiClient;
    protected android.location.Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        FacebookSdk.sdkInitialize(getApplicationContext());

        buildGoogleApiClient();
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
                            String profilePictureUrl = "http://graph.facebook.com/" + object.getString("id") + "/picture?type=large";
                            launchMainActivity(object.getString("name"), object.getString("first_name"), object.getString("last_name"), object.getString("email"), profilePictureUrl);
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

    private void launchMainActivity(final String name, final String fName, final String lName, final String email, final String pic) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                i.putExtra("fName", fName);
                i.putExtra("lName", lName);
                i.putExtra("name", name);
                i.putExtra("email", email);
                i.putExtra("url", pic);
                if (mLastLocation != null) {
                    i.putExtra("lon", mLastLocation.getLongitude());
                    i.putExtra("lat", mLastLocation.getLatitude());
                } else {
                    i.putExtra("lon", 0);
                    i.putExtra("lat", 0);
                }
                startActivity(i);
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
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
}
