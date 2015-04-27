package com.designforamerica.seek;

/**
 * Created by jbruzek on 3/26/15.
 */
public class Location {

    private String name;
    private double longitude;
    private double latitude;
    private Boolean def; //default is a reserved keyword

    public Location(String n, double la, double lo, boolean d) {
        this.name = n;
        this.latitude = la;
        this.longitude =lo;
        this.def = d;
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

    public Boolean def() {
        return def;
    }
}
