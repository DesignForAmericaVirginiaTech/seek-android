package com.designforamerica.seek;

/**
 * Created by jbruzek on 3/26/15.
 */
public class Location {

    private String name;
    private double longitude;
    private double latitude;

    public Location(String n, double la, double lo) {
        this.name = n;
        this.latitude = la;
        this.longitude =lo;
    }

    public double latitude() {
        return latitude;
    }

    public double longitude() {
        return longitude;
    }

    public String name() {
        return name;
    }
}
