package com.designforamerica.seek;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jbruzek on 3/26/15.
 */
public class ParseHelper {
    private ParseCallbacks pc;
    private ArrayList<Location> locations;

    /**
     * initilize the helper with a callback
     * @param p
     */
    public ParseHelper(ParseCallbacks p) {
        pc = p;
        locations = new ArrayList<Location>();
    }

    /**
     * query for all the locations
     */
    public void queryLocations() {
        locations.clear();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("locations");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> locList, ParseException e) {
                if (e == null) {
                    Log.d("Query", "Found the locations. " + locList.size() + " locations found.");
                    for(ParseObject p : locList) {
                        Location l = new Location(p.getString("name"), p.getDouble("latitude"), p.getDouble("longitude"));
                        locations.add(l);
                    }
                    pc.complete();
                } else {
                    Log.d("Query", "Error: " + e.getMessage());
                }
            }
        });
    }

    /**
     * query for a specific user's locations
     * @param user
     */
    public void queryMyLocations(String user) {
        locations.clear();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("locations");
        query.whereEqualTo("uid", user);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> locList, ParseException e) {
                if (e == null) {
                    Log.d("Query", "User locations. " + locList.size() + " locations found.");
                    for(ParseObject p : locList) {
                        Location l = new Location(p.getString("name"), p.getDouble("latitude"), p.getDouble("longitude"));
                        locations.add(l);
                    }
                    pc.complete();
                } else {
                    Log.d("Query", "Error: " + e.getMessage());
                }
            }
        });
    }

    /**
     * query for a user's favorite locations
     * @param user
     */
    public void queryFavoriteLocations(String user) {
        locations.clear();
        pc.complete();
    }

    /**
     * return the arraylist of locations
     * @return
     */
    public ArrayList<Location> getLocations() {
        return locations;
    }
}
