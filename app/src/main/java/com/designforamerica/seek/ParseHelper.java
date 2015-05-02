package com.designforamerica.seek;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
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
    private ArrayList<Distance> distances;
    private ArrayList<String> favorites;
    private ArrayList<Location> myLocations;

    /**
     * initilize the helper with a callback
     * @param p
     */
    public ParseHelper(ParseCallbacks p) {
        pc = p;
        locations = new ArrayList<Location>();
        distances = new ArrayList<Distance>();
        favorites = new ArrayList<String>();
        myLocations = new ArrayList<Location>();
    }

    /**
     * query for the default locations
     */
    public void queryLocations() {
        locations.clear();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("locations");
        query.whereEqualTo("default", true);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> locList, ParseException e) {
                if (e == null) {
                    for(ParseObject p : locList) {
                        Location l = new Location(p.getString("name"), p.getDouble("latitude"), p.getDouble("longitude"), p.getBoolean("default"), p.getObjectId());
                        locations.add(l);
                    }
                    Seek.setLocations(locations);
                    pc.complete(locations.isEmpty());
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
                    for(ParseObject p : locList) {
                        Location l = new Location(p.getString("name"), p.getDouble("latitude"), p.getDouble("longitude"), p.getBoolean("default"), p.getObjectId());
                        myLocations.add(l);
                    }
                    Seek.setMyLocations(myLocations, myLocations.isEmpty());
                    pc.complete(myLocations.isEmpty());
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
        ParseQuery<ParseObject> query = ParseQuery.getQuery("favorites");
        query.whereEqualTo("uid", user);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for(ParseObject p : list) {
                        favorites.add(p.getString("lid"));
                    }
                    Seek.setFavorites(favorites);
                    pc.complete(favorites.isEmpty());
                } else {
                    Log.d("Query", "Error: " + e.getMessage());
                }
            }
        });
    }

    /**
     * query for the distances
     */
    public void queryDistances() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("distances");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for(ParseObject p : list) {
                        Distance d = new Distance(p.getString("startId"), p.getString("endId"), p.getDouble("time"), p.getInt("type"));
                        distances.add(d);
                    }
                    Seek.setDistances(distances);
                    pc.complete(distances.isEmpty());
                } else {
                    Log.d("Query", "Error: " + e.getMessage());
                }
            }
        });
    }

    /**
     * add a location object in the background
     * @param id
     */
    public void addFavorite(String id) {
        ParseUser user = ParseUser.getCurrentUser();
        ParseObject favorite = new ParseObject("favorites");
        favorite.put("uid", user.getObjectId());
        favorite.put("lid", id);
        favorite.saveInBackground();
    }

    /**
     * remove a favorite from the database
     * @param id
     */
    public void removeFavorite(String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("favorites");
        query.whereEqualTo("lid", id);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject p : list) {
                        p.deleteInBackground();
                    }
                } else {
                    Log.d("Query", "Error: " + e.getMessage());
                }
            }
        });
    }

    /**
     * update a location in the database
     * @param l
     */
    public void update(final Location l) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("locations");

        // Retrieve the object by id
        query.getInBackground(l.id(), new GetCallback<ParseObject>() {
            public void done(ParseObject obj, ParseException e) {
                if (e == null) {
                    obj.put("name", l.name());
                    obj.put("latitude", l.latitude());
                    obj.put("longitude", l.longitude());
                    obj.saveInBackground();
                }
            }
        });
    }

    /**
     * return the arraylist of locations
     * @return
     */
    public ArrayList<Location> getLocations() {
        return locations;
    }
}
