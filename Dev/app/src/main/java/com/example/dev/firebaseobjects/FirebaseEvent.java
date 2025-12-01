package com.example.dev.firebaseobjects;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data Model representing the Event document stored in Firebase database.
 */
public class FirebaseEvent {
    private String eventId;
    private String organizerId;
    private String eventName;
    private String location;
    private String eventDate;
    private String eventTime;
    private String eventStart;
    private String eventEnd;
    @Nullable
    private String posterUrl;
    @Nullable
    private String posterUri;
    private int attendingCount;
    private boolean locationRequired;
    private List<Map<String, Object>> waitingList = new ArrayList<>();

    private boolean isWaitlistLimited;
    private int waitlistLimit;

    public FirebaseEvent() {
    }

    public FirebaseEvent(String eventId, String organizerId, String eventName, String location, String eventDate, String eventTime, String eventStart, String eventEnd, @Nullable String posterUrl, int attendingCount, boolean locationRequired) {
        this.eventId = eventId;
        this.organizerId = organizerId;
        this.eventName = eventName;
        this.location = location;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.posterUrl = posterUrl;
        this.attendingCount = attendingCount;
        this.locationRequired = locationRequired;
    }

    // New Constructor primarily for Waitlist func
    public FirebaseEvent(String eventId, String organizerId, String eventName, String location, String eventDate, String eventTime, String eventStart, String eventEnd, @Nullable String posterUrl, int attendingCount, boolean locationRequired, boolean isWaitlistLimited, int waitlistLimit) {
        this.eventId = eventId;
        this.organizerId = organizerId;
        this.eventName = eventName;
        this.location = location;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.posterUrl = posterUrl;
        this.attendingCount = attendingCount;
        this.locationRequired = locationRequired;
        this.isWaitlistLimited = isWaitlistLimited;
        this.waitlistLimit =waitlistLimit;
    }
    


    public String getEventId() {
        return eventId;
    }
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getOrganizerId() {
        return organizerId;
    }
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public String getEventName() {
        return eventName;
    }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getEventDate() {
        return eventDate;
    }
    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }
    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventStart() {
        return eventStart;
    }
    public void setEventStart(String eventStart) {
        this.eventStart = eventStart;
    }

    public String getEventEnd() {
        return eventEnd;
    }
    public void setEventEnd(String eventEnd) {
        this.eventEnd = eventEnd;
    }

    @Nullable
    public String getPosterUrl() {
        return posterUrl;
    }
    public void setPosterUrl(@Nullable String posterUrl) {
        this.posterUrl = posterUrl;
    }

    @Nullable
    public String getPosterUri() {
        return posterUri;
    }
    public void setPosterUri(@Nullable String posterUri) {
        this.posterUri = posterUri;
    }

    public int getAttendingCount() {
        return attendingCount;
    }
    public void setAttendingCount(int attendingCount) {
        this.attendingCount = attendingCount;
    }

    public boolean isLocationRequired() {
        return locationRequired;
    }
    public void setLocationRequired(boolean locationRequired) {
        this.locationRequired = locationRequired;
    }
    public boolean isWaitlistLimited(){
        return isWaitlistLimited;
    }
    public void setWaitlistLimited(boolean waitlistLimited){
        isWaitlistLimited = waitlistLimited;
    }

    public int getWaitlistLimit(){
        return waitlistLimit;
    }

    public void setWaitlistLimit(int waitlistLimit){
        this.waitlistLimit = waitlistLimit;
    }

    public List<Map<String, Object>> getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(List<Map<String, Object>> waitingList) {
        this.waitingList = waitingList;
    }
    public void addEntrantToWaitingList(Map<String, Object> entrantData) {
        String entrantId = (String) entrantData.get("entrantId");
        if (entrantId == null) {
            return;
        }

        for (Map<String, Object> existingEntrant : waitingList) {
            if (entrantId.equals(existingEntrant.get("entrantId"))) {
                return; // already exists
            }
        }
        waitingList.add(entrantData);
    }
    public void removeEntrantFromWaitingList(String entrantId) {
        if (entrantId == null) {
            return;
        }
        waitingList.removeIf(entrant -> entrantId.equals(entrant.get("entrantId")));
    }
    public void updateEntrantStatus(String entrantId, String newStatus) {
        if (entrantId == null || newStatus == null) {
            return;
        }

        for (Map<String, Object> entrant : waitingList) {
            if (entrantId.equals(entrant.get("entrantid"))) {
                entrant.put("status", newStatus);
                break;
            }
        }
    }
}
