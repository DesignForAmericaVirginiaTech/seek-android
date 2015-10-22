package com.seekdfa.seek.models;

/**
 * Created by jbruzek on 3/26/15.
 */
public class Location {

    private String name;
    private double longitude;
    private double latitude;
    private Boolean def; //default is a reserved keyword
    private String id;
    private Boolean favorite;

    public Location(String n, double la, double lo, boolean d, String i) {
        this.name = n;
        this.latitude = la;
        this.longitude =lo;
        this.def = d;
        this.id = i;
        favorite = false;
    }

    public void favorite(boolean fav) {
        favorite = fav;
    }

    public boolean favorite() {
        return favorite;
    }

    public double latitude() {
        return latitude;
    }

    public void latitude(double lat) {
        latitude = lat;
    }

    public double longitude() {
        return longitude;
    }

    public void longitude(double lon) {
        longitude = lon;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public Boolean def() {
        return def;
    }

    public String id() {
        return id;
    }

    public void id(String id) {
        this.id = id;
    }
}
