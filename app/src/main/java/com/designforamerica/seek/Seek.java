package com.designforamerica.seek;

import android.app.Application;
import android.provider.Settings;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParsePush;
import com.parse.SaveCallback;

/**
 * Created by jbruzek on 3/26/15.
 */
public class Seek extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //Do Parse stuff
        Parse.initialize(this, "L7CDrAJbInWtaIKgDgd2KEdwrMTMmoUkrJ9NqKI6", "0RYtKnmHQndqQJnEgox9ooOe245N70qavryxV6vY");
        ParseFacebookUtils.initialize(getApplicationContext());


        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }
}
