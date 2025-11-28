/**
 * EntrantEvent (Model)
 *
 * Represents a single event from Firestore.
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

    public EntrantEvent() {}

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

    public int getAttendingCount() { return attendingCount; }
    public String getEventDate() { return eventDate; }
    public String getEventEnd() { return eventEnd; }
    public String getEventId() { return eventId; }
    public String getEventName() { return eventName; }
    public String getEventStart() { return eventStart; }
    public String getEventTime() { return eventTime; }
    public String getLocation() { return location; }
    public boolean isLocationRequired() { return locationRequired; }

    public void setAttendingCount(int attendingCount) { this.attendingCount = attendingCount; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }
    public void setEventEnd(String eventEnd) { this.eventEnd = eventEnd; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public void setEventStart(String eventStart) { this.eventStart = eventStart; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }
    public void setLocation(String location) { this.location = location; }
    public void setLocationRequired(boolean locationRequired) { this.locationRequired = locationRequired; }
}
