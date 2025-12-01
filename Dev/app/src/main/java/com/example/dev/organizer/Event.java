package com.example.dev.organizer;

/**
 * Data model relevant to the events shown on the Organizer Dashboard
 * CLass contains the main display information about an event, collects data from Firebase.
 */

public class Event {
    private String eventId;
    private String title;
    private String category;
    private String location;
    private String closingTime;
    private int capacity;

    /**
     * Constructs new Event object for display on the dashboard
     * @param eventId: Unique ID of the event
     * @param eventName: Name of the event
     * @param category: Classification of the event (Uses placeholder for now)
     * @param location: Location of the event
     * @param eventClosing: Registration End
     * @param capacity: Placeholder used for now
     */
    public Event(String eventId, String eventName, String category, String location, String eventClosing, int capacity){
        this.eventId = eventId;
        this.title = eventName;
        this.category = category;
        this.location = location;
        this.closingTime = eventClosing;
        this.capacity = capacity;

    }

    public String getEventId(){
        return eventId;
    }
    public String getTitle(){
        return title;
    }
    public String getCategory(){
        return category;
    }
    public String getLocation(){
        return location;
    }
    public String getClosingTime(){
        return closingTime;
    }
    public int getCapacity(){
        return capacity;
    }





}
