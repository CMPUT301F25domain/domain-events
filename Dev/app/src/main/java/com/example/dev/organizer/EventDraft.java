package com.example.dev.organizer;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Holds organizer input while navigating through the multi-step event creation flow.
 */
public class EventDraft implements Parcelable {
    private final String organizerId;
    private final String eventName;
    private final String location;
    private final String eventDate;
    private final String eventTime;
    private final String registrationStart;
    private final String registrationEnd;
    private final String posterUri;
    private final boolean locationRequired;
    private final boolean isWaitlistLimited;
    private final int waitlistLimit;


    public EventDraft(
            @Nullable String organizerId,
            @Nullable String eventName,
            @Nullable String location,
            @Nullable String eventDate,
            @Nullable String eventTime,
            @Nullable String registrationStart,
            @Nullable String registrationEnd,
            @Nullable String posterUri,
            boolean locationRequired,
            boolean isWaitlistLimited,
            int waitlistLimit) {

        this.organizerId = organizerId;
        this.eventName = eventName;
        this.location = location;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.registrationStart = registrationStart;
        this.registrationEnd = registrationEnd;
        this.posterUri = posterUri;
        this.locationRequired = locationRequired;
        this.isWaitlistLimited = isWaitlistLimited;
        this.waitlistLimit = waitlistLimit;

    }

    protected EventDraft(Parcel in) {
        organizerId = in.readString();
        eventName = in.readString();
        location = in.readString();
        eventDate = in.readString();
        eventTime = in.readString();
        registrationStart = in.readString();
        registrationEnd = in.readString();
        posterUri = in.readString();
        locationRequired = in.readByte() != 0;
        isWaitlistLimited = in.readByte() != 0;
        waitlistLimit = in.readInt();

    }

    public static final Creator<EventDraft> CREATOR = new Creator<EventDraft>() {
        @Override
        public EventDraft createFromParcel(Parcel in) {
            return new EventDraft(in);
        }

        @Override
        public EventDraft[] newArray(int size) {
            return new EventDraft[size];
        }
    };

    @Nullable
    public String getOrganizerId() {
        return organizerId;
    }

    @Nullable
    public String getEventName() {
        return eventName;
    }

    @Nullable
    public String getLocation() {
        return location;
    }

    @Nullable
    public String getEventDate() {
        return eventDate;
    }

    @Nullable
    public String getEventTime() {
        return eventTime;
    }

    @Nullable
    public String getRegistrationStart() {
        return registrationStart;
    }

    @Nullable
    public String getRegistrationEnd() {
        return registrationEnd;
    }

    @Nullable
    public String getPosterUri() {
        return posterUri;
    }

    public boolean isLocationRequired() {
        return locationRequired;
    }

    public boolean isWaitlistLimited(){
        return isWaitlistLimited;
    }

    public int getWaitlistLimit(){
        return waitlistLimit;
    }

    public EventDraft withPosterUri(@Nullable String newPosterUri) {
        return new EventDraft(organizerId, eventName, location, eventDate, eventTime, registrationStart, registrationEnd, newPosterUri, locationRequired,isWaitlistLimited,waitlistLimit);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(organizerId);
        dest.writeString(eventName);
        dest.writeString(location);
        dest.writeString(eventDate);
        dest.writeString(eventTime);
        dest.writeString(registrationStart);
        dest.writeString(registrationEnd);
        dest.writeString(posterUri);
        dest.writeByte((byte) (locationRequired ? 1 : 0));
        dest.writeByte((byte) (isWaitlistLimited ? 1 : 0));
        dest.writeInt(waitlistLimit);
    }
}
