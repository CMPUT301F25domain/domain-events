package com.example.entrantscreenactivity.models;

public class Event {

    private String eventName;
    private String location;
    private String date;

    public Event(String eventName, String location, String date) {
        this.eventName = eventName;
        this.location = location;
        this.date = date;
    }

    public String getEventName() { return eventName; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
}
