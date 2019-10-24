package com.quigglesproductions.paulq.calendartest;

public class Location {
    private String name;
    private String location;

    public Location(String name, String location)
    {
        this.name = name;
        this.location = location;
    }

    public String getName(){
        return this.name;
    }

    public String getLocation(){
        return this.location;
    }
}
