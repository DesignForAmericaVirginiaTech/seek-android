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

import java.util.ArrayList;

/**
 * Application class for Seek
 * Firstly, the application initializes a Parse connection on start
 *
 * Additionally, the application acts as a singleton in the code, and stores locations
 * and distance information from the database that is loaded on startup.
 *
 * The number of distances will never vary from the <100 elements in the db
 * If, somehow, the user has stored an unreal number of custom locations, the
 * size of locations will be limited to MAX_LIST_SIZE
 * The same is true for user favorites.
 *
 * Created by jbruzek on 3/26/15.
 */
public class Seek extends Application {
    private final static int MAX_LIST_SIZE = 10000;
    private static ArrayList<Location> locations;
    private static ArrayList<Distance> distances;
    private static ArrayList<String> favorites;
    private static boolean favorites_empty;
    private static ArrayList<Location> myLocations;
    private static boolean myLocations_empty;

    //User information, grabbable from anywhere in the app publicly
    //These values are set once in the splash screen.
    private static String name;
    private static String email;
    private static String firstName;
    private static String lastName;
    private static String profilePic;
    private static String coverPic;


    @Override
    public void onCreate() {
        super.onCreate();

        //Do Parse stuff
        //OLD ONE
        //Parse.initialize(this, "L7CDrAJbInWtaIKgDgd2KEdwrMTMmoUkrJ9NqKI6", "0RYtKnmHQndqQJnEgox9ooOe245N70qavryxV6vY");
        //NEW ONE
        Parse.initialize(this, "csMOVllLLOUIenFmTG1oH6ayhdinQnWpYr0VwGIx", "WuGAVhEuRXvd2snx7x3O7wvPKisZwLHUHyaa5fTt");
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

    /**
     * return if all the information that needs to be loaded is loaded.
     * @return
     */
    public static boolean complete() {
        return (locations != null && distances != null && favorites != null && myLocations != null
                    && name != null && email != null && profilePic != null && coverPic != null);
    }

    public static void setProfileInformation(String n, String fn, String ln, String e, String pp, String cp) {
        name = n;
        firstName = fn;
        lastName = ln;
        email = e;
        profilePic = pp;
        coverPic = cp;
    }

    public static void setLocations(ArrayList<Location> loc) {
        locations = loc;
    }

    public static ArrayList<Location> getLocations() {
        ArrayList<Location> result = new ArrayList<Location>(locations.size() + myLocations.size());
        result.addAll(locations);
        result.addAll(myLocations);
        return result;
    }

    public static void setDistances(ArrayList<Distance> dis) {
        distances = dis;
    }

    public static ArrayList<Distance> getDistances() {
        return distances;
    }

    public static void setFavorites(ArrayList<String> fav, boolean fempty) {
        favorites = fav;
        favorites_empty = fempty;
    }

    public static ArrayList<String> getFavorites() {
        return favorites;
    }

    public static boolean getFavoritesEmpty() {
        return favorites_empty;
    }

    public static ArrayList<Location> getMyLocations() {
        return myLocations;
    }

    public static boolean getMyLocationsEmpty() {
        return myLocations_empty;
    }

    public static void setMyLocations(ArrayList<Location> mloc, boolean mlempty) {
        myLocations = mloc;
        myLocations_empty = mlempty;
    }

    public static String getName() {
        return name;
    }

    public static String getEmail() {
        return email;
    }

    public static String getFirstName() {
        return firstName;
    }

    public static String getLastName() {
        return lastName;
    }

    public static String getProfilePic() {
        return profilePic;
    }

    public static String getCoverPic() {
        return coverPic;
    }

    /**
     * return an arrayList of your favorite locations
     * @return
     */
    public static ArrayList<Location> getFavoriteLocations() {
        ArrayList<Location> results = new ArrayList<Location>();

        for (int i = 0; i < locations.size(); i++) {
            if (favorites.contains(locations.get(i).id())) {
                results.add(locations.get(i));
            }
        }

        return results;
    }
}
