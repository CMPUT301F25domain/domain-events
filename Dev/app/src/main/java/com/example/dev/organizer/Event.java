package com.example.dev.organizer;

public class Event {
    private String eventId;
    private String title;
    private String category;
    private String location;
    private String closingTime;
    private int capacity;

    public Event(String eventId, String title, String category, String location, String closingTime,int capacity){
        this.eventId = eventId;
        this.title = title;
        this.category = category;
        this.location = location;
        this.closingTime =closingTime;
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
