package com.example.dev.firebaseobjects;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.Set;

/**
 * Data Model representing the Event document stored in Firebase database.
 */
public class FirebaseEvent {
    private String eventId;
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
    private Set<String> wishListAccounts;

    public FirebaseEvent() {
        // Needed for Firestore serialization
    }

    public FirebaseEvent(String eventId,
                         String eventName,
                         String location,
                         String eventDate,
                         String eventTime,
                         String eventStart,
                         String eventEnd,
                         @Nullable String posterUrl,
                         int attendingCount) {
        this(eventId, eventName, location, eventDate, eventTime, eventStart, eventEnd, posterUrl, attendingCount, false);
        this.wishListAccounts = Collections.<String> emptySet();
    }

    public FirebaseEvent(String eventId,
                         String eventName,
                         String location,
                         String eventDate,
                         String eventTime,
                         String eventStart,
                         String eventEnd,
                         int attendingCount,
                         boolean locationRequired) {
        this(eventId, eventName, location, eventDate, eventTime, eventStart, eventEnd, null, attendingCount, locationRequired);
        this.wishListAccounts = Collections.<String> emptySet();
    }

    public FirebaseEvent(String eventId,
                         String eventName,
                         String location,
                         String eventDate,
                         String eventTime,
                         String eventStart,
                         String eventEnd,
                         @Nullable String posterUrl,
                         int attendingCount,
                         boolean locationRequired) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.location = location;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        setPosterUrl(posterUrl);
        this.attendingCount = attendingCount;
        this.locationRequired = locationRequired;
        this.wishListAccounts = Collections.<String> emptySet();
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

    public String getEventDate() {
        return eventDate;
    }
    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
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
        if (posterUrl != null && !posterUrl.isEmpty()) {
            return posterUrl;
        }
        return posterUri;
    }

    public void setPosterUrl(@Nullable String posterUrl) {
        this.posterUrl = posterUrl;
        this.posterUri = posterUrl;
    }

    /**
     * Backwards compatible accessor for older code paths still expecting a URI-named field.
     */
    @Deprecated
    @Nullable
    public String getPosterUri() {
        return getPosterUrl();
    }

    @Deprecated
    public void setPosterUri(@Nullable String posterUri) {
        this.posterUri = posterUri;
        if (posterUrl == null || posterUrl.isEmpty()) {
            posterUrl = posterUri;
        }
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

    public boolean existsInWishListEvents(FirebaseAccount account) {
        String accountID = account.getAccountID();
        return wishListAccounts.contains(accountID);
    }
    public void addToWishListEvents(FirebaseAccount account) {
        String accountID = account.getAccountID();
        wishListAccounts.add(accountID);
    }
    public void removeFromWishListEvents(FirebaseAccount account) {
        String accountID = account.getAccountID();
        wishListAccounts.remove(accountID);
    }
}
