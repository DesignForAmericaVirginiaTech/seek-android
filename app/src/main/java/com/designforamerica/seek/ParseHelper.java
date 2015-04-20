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

    public ParseHelper(ParseCallbacks p) {
        pc = p;
        locations = new ArrayList<Location>();
    }

    public void queryLocations() {
        locations.clear();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("signs");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> locList, ParseException e) {
                if (e == null) {
                    Log.d("Query", "Found the locations. " + locList.size() + " locations found.");
                    for(ParseObject p : locList) {
                        Location l = new Location(p.getString("Name"), p.getDouble("Latitude"), p.getDouble("Longitude"));
                        locations.add(l);
                    }
                    pc.complete();
                } else {
                    Log.d("Query", "Error: " + e.getMessage());
                }
            }
        });
    }

    public void queryMyLocations(ParseUser user) {
        //TODO: implement this
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }
}
