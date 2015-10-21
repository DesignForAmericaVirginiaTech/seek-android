package com.designforamerica.seek.utilities;

/**
 * Helper class to get distances between different locations
 *
 *
 * Created by jbruzek on 10/21/15.
 */
public class Distances {

    /**
     * Get the distance between two latlng points.
     * Taken from here:
     * http://www.geodatasource.com/developers/java
     *
     * Slightly edited so it rounds to one decimal place
     */
    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 10;
        long tmp = Math.round(dist);
        return (double) tmp / 10;
    }

    /**
     * used by distance()
     */
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /**
     * used by distance()
     */
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
