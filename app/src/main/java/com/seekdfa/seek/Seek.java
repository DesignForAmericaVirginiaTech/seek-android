package com.seekdfa.seek;

import android.app.Application;
import android.provider.Settings;
import android.util.Log;

import com.seekdfa.seek.interfaces.LocationListener;
import com.seekdfa.seek.interfaces.ParseCallbacks;
import com.seekdfa.seek.models.Distance;
import com.seekdfa.seek.models.Location;
import com.seekdfa.seek.utilities.Apis;
import com.seekdfa.seek.utilities.Distances;
import com.google.android.gms.maps.model.LatLng;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParsePush;
import com.parse.SaveCallback;
import com.seekdfa.seek.utilities.Profile;

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
public class Seek extends Application implements ParseCallbacks {
    private final static int MAX_LIST_SIZE = 10000;
    private static ArrayList<Location> locations;
    private static ArrayList<Distance> distances;
    private static ArrayList<String> favorites;
    private static boolean favorites_empty;
    private static ArrayList<Location> myLocations;
    private static boolean myLocations_empty;
    private static ArrayList<Location> favoriteLocations;
    private static ArrayList<Location> allLocations;

    private static android.location.Location lastLocation;

    /* A list of all the LocationListeners that depend on callbacks */
    private static ArrayList<LocationListener> listeners;

    //User information, grabbable from anywhere in the app publicly
    //These values are set once in the splash screen

    private static ParseHelper ph;


    @Override
    public void onCreate() {
        super.onCreate();

        listeners = new ArrayList<LocationListener>();
        ph = new ParseHelper(this);

        //give context to utilities
        Profile.setContext(getApplicationContext());
        Apis.setContext(getApplicationContext());

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
     * Get the last location that has been reported to Seek
     * @return
     */
    public static android.location.Location getLastLocation() {
        return lastLocation;
    }

    /**
     * Set the last location.
     * @param lastLocation
     */
    public static void setLastLocation(android.location.Location lastLocation) {
        Seek.lastLocation = lastLocation;
    }

    /**
     * register a locationListener. Add it to the list
     * @param ll
     */
    public static void registerListener(LocationListener ll) {
        listeners.add(ll);
    }

    /**
     * alert all the locations that there has been a change in the data
     */
    private static void alertListeners() {
        for (LocationListener l : listeners) {
            l.locationsChanged();
        }
    }

    /**
     * get a location by the location id
     *
     * returns null if no location found
     */
    public static Location getLocation(String id) {
        for (Location l : allLocations) {
            if (l.id().equals(id)) {
                return l;
            }
        }
        return null;
    }

    /**
     * return if all the information that needs to be loaded is loaded.
     * @return
     */
    public static boolean complete() {
        return (locations != null && distances != null && favorites != null && myLocations != null
                    && Profile.isLoaded());
    }

//    public static String getAnonymousId() {
//        //return Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
//    }

    /**
     * Go through default locations and myLocations to make one locations list
     * then add the favorite locations to their list.
     */
    public static void processLocations() {
        allLocations = new ArrayList<Location>(locations.size() + myLocations.size());
        allLocations.addAll(locations);
        allLocations.addAll(myLocations);
        Distances.sortByDistance(allLocations);
        processFavorites();
    }

    /**
     * go through the favorites list and the locations list to make a list of favorite locations
     */
    public static void processFavorites() {
        favoriteLocations = new ArrayList<Location>();

        for (int i = 0; i < allLocations.size(); i++) {
            if (favorites.contains(allLocations.get(i).id())) {
                favoriteLocations.add(allLocations.get(i));
                allLocations.get(i).favorite(true);
            }
        }

        favorites_empty = favoriteLocations.isEmpty();
    }

    /**
     * set a location to favorite
     * @param id
     */
    public static void addFavorite(String id) {
        for (int i = 0; i < allLocations.size(); i++) {
            if (allLocations.get(i).id().equals(id)) {
                allLocations.get(i).favorite(true);
                if (!favoriteLocations.contains(allLocations.get(i))) {
                    favoriteLocations.add(allLocations.get(i));
                }
                ph.addFavorite(id);
            }
        }
        favorites_empty = favoriteLocations.isEmpty();
        alertListeners();
    }

    /**
     * remove favorite from a location
     * @param id
     */
    public static void removeFavorite(String id) {
        for (int i = 0; i < favoriteLocations.size(); i++) {
            if (favoriteLocations.get(i).id().equals(id)) {
                favoriteLocations.get(i).favorite(false);
                favoriteLocations.remove(i);
                ph.removeFavorite(id);
            }
        }
        favorites_empty = favoriteLocations.isEmpty();
        alertListeners();
    }

    /**
     * delete the location from all the lists and the parse database
     * @param l
     */
    public static void deleteLocation(Location l) {
        allLocations.remove(l);
        favoriteLocations.remove(l);
        myLocations.remove(l);

        ph.remove(l);
        alertListeners();
    }

    /**
     * update a location with new information
     * @param l the location to update
     * @param name
     * @param newLoc
     */
    public static void updateLocation(Location l, String name, LatLng newLoc) {
        l.name(name);
        l.latitude(newLoc.latitude);
        l.longitude(newLoc.longitude);

        ph.update(l);
        alertListeners();
    }

    /**
     * add a location to the database and lists
     * @param l
     */
    public static void addLocation(Location l) {
        allLocations.add(l);
        myLocations.add(l);

        Distances.sortByDistance(allLocations);
        Distances.sortByDistance(myLocations);

        ph.addLocation(l);
        alertListeners();
    }

    public static void setLocations(ArrayList<Location> loc) {
        locations = loc;
        Distances.sortByDistance(locations);
    }

    public static ArrayList<Location> getLocations() {
        return allLocations;
    }

    public static void setDistances(ArrayList<Distance> dis) {
        distances = dis;
    }

    public static ArrayList<Distance> getDistances() {
        return distances;
    }

    public static void setFavorites(ArrayList<String> fav) {
        favorites = fav;
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
        Distances.sortByDistance(myLocations);
        myLocations_empty = mlempty;
    }

    public static ArrayList<Location> getFavoriteLocations() {
        return favoriteLocations;
    }

    /**
     * when the parsehelper returns a result
     * @param empty
     */
    @Override
    public void complete(boolean empty) {
        //cool beans
    }
}
