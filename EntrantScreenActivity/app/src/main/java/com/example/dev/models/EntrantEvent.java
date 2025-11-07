package com.example.dev.models;

public class Event {

    private String eventName;
    private String location;
    private String eventDate;

    public Event() {} // Required by Firestore

    public String getEventName() { return eventName; }
    public String getLocation() { return location; }
    public String getEventDate() { return eventDate; }
}
