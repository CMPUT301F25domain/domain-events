package com.example.dev.organizer;

public class FirebaseEvent {
    private String eventId;
    private String eventName;
    private String location;
    private String eventDate;
    private String eventTime;
    private String eventStart;
    private String eventEnd;
    private int attendingCount;
    private boolean locationRequired;


    public FirebaseEvent(){

    }

    public FirebaseEvent(String eventId, String eventName, String location, String eventDate, String eventTime,String eventStart, String eventEnd, int attendingCount, boolean locationRequired){
        this.eventId = eventId;
        this.eventName = eventName;
        this.location = location;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.attendingCount = attendingCount;
        this.locationRequired = locationRequired;

    }

    public String getEventId(){
        return eventId;
    }
    public void setEventId(String eventId){
        this.eventId=eventId;
    }
    public String getEventName(){
        return eventName;
    }
    public void setEventName(String eventName){
        this.eventName=eventName;
    }

    public String getEventDate(){
        return eventDate;
    }
    public void setEventDate(String eventDate){
        this.eventDate=eventDate;
    }

    public String getLocation(){
        return location;
    }
    public void setLocation(String location){
        this.location=location;
    }
    public String getEventTime(){
        return eventTime;
    }
    public void setEventTime(String eventTime){
        this.eventTime=eventTime;
    }

    public String getEventStart(){
        return eventStart;
    }
    public void setEventStart(String eventStart){
        this.eventStart=eventStart;
    }

    public String getEventEnd(){
        return eventEnd;
    }
    public void setEventEnd(String eventEnd){
        this.eventEnd=eventEnd;
    }
    public int getAttendingCount(){
        return attendingCount;
    }
    public void setAttendingCount(int attendingCount){
        this.attendingCount=attendingCount;
    }

    public boolean isLocationRequired(){
        return locationRequired;
    }

    public void setLocationRequired(boolean locationRequired) {
        this.locationRequired = locationRequired;
    }
}
