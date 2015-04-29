package com.designforamerica.seek;

/**
 * Created by jbruzek on 3/26/15.
 */
public class Location {

    private String name;
    private double longitude;
    private double latitude;
    private Boolean def; //default is a reserved keyword
    private String id;

    public Location(String n, double la, double lo, boolean d, String i) {
        this.name = n;
        this.latitude = la;
        this.longitude =lo;
        this.def = d;
        this.id = i;
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

    public String id() {
        return id;
    }
}
