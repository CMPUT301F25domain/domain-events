package com.example.dev.organizer;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Holds organizer input while navigating through the multi-step event creation flow.
 */
public class EventDraft implements Parcelable {
    private final String eventName;
    private final String location;
    private final String eventDate;
    private final String eventTime;
    private final String registrationStart;
    private final String registrationEnd;

    public EventDraft(@Nullable String eventName,
                      @Nullable String location,
                      @Nullable String eventDate,
                      @Nullable String eventTime,
                      @Nullable String registrationStart,
                      @Nullable String registrationEnd) {
        this.eventName = eventName;
        this.location = location;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.registrationStart = registrationStart;
        this.registrationEnd = registrationEnd;
    }

    protected EventDraft(Parcel in) {
        eventName = in.readString();
        location = in.readString();
        eventDate = in.readString();
        eventTime = in.readString();
        registrationStart = in.readString();
        registrationEnd = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(eventName);
        dest.writeString(location);
        dest.writeString(eventDate);
        dest.writeString(eventTime);
        dest.writeString(registrationStart);
        dest.writeString(registrationEnd);
    }
}