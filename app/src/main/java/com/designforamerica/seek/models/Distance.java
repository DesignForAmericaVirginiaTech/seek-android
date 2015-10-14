package com.designforamerica.seek.models;

/**
 * A distance class contains the information of one element in the distance table
 * Created by jbruzek on 4/29/15.
 */
public class Distance {

    private int type;
    private double time;
    private String start;
    private String end;

    public Distance(String s, String e, double t, int ty) {
        start = s;
        end = end;
        time = t;
        type = ty;
    }

    public int getType() {
        return type;
    }

    public double getTime() {
        return time;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }
}
