/**
 * EntrantEvent
 *
 * Model class representing an event as seen by entrant users.
 * This class maps directly to Firestore documents stored in the "events" collection.
 */

package com.example.dev.entrant.models;

public class EntrantEvent {

    private int attendingCount;
    private String eventDate;
    private String eventEnd;
    private String eventId;
    private String eventName;
    private String eventStart;
    private String eventTime;
    private String location;
    private boolean locationRequired;
    private boolean isSignedUp;

    public EntrantEvent() { }

    public EntrantEvent(int attendingCount, String eventDate, String eventEnd, String eventId,
                        String eventName, String eventStart, String eventTime,
                        String location, boolean locationRequired) {

        this.attendingCount = attendingCount;
        this.eventDate = eventDate;
        this.eventEnd = eventEnd;
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventStart = eventStart;
        this.eventTime = eventTime;
        this.location = location;
        this.locationRequired = locationRequired;
    }

    public int getAttendingCount() {
        return attendingCount;
    }
    public void setAttendingCount(int attendingCount) {
        this.attendingCount = attendingCount;
    }

    public String getEventDate() {
        return eventDate;
    }
    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventEnd() {
        return eventEnd;
    }
    public void setEventEnd(String eventEnd) {
        this.eventEnd = eventEnd;
    }

    public String getEventId() {
        return eventId;
    }
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventStart() {
        return eventStart;
    }
    public void setEventStart(String eventStart) {
        this.eventStart = eventStart;
    }

    public String getEventTime() {
        return eventTime;
    }
    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isLocationRequired() {
        return locationRequired;
    }
    public void setLocationRequired(boolean locationRequired) {
        this.locationRequired = locationRequired;
    }

    public boolean isSignedUp() {
        return isSignedUp;
    }
    public void setSignedUp(boolean signedUp) {
        isSignedUp = signedUp;
    }

}
