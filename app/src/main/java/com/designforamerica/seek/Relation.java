package com.designforamerica.seek;

/**
 * Created by jbruzek on 4/29/15.
 */
public class Relation<E, T> {
    private E first;
    private T second;

    public Relation(E first, T second) {
        this.first = first;
        this.second = second;
    }

    public E first() {
        return first;
    }

    public T second() {
        return second;
    }
}
