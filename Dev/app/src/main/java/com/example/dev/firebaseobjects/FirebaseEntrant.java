package com.example.dev.firebaseobjects;

import java.util.ArrayList;
import java.util.List;

public class FirebaseEntrant {
    private String email;
    private String name;
    private String phone;
    private List<String> joinedEvents = new ArrayList<>();
    private List<String> waitlistedEvents = new ArrayList<>();

    public FirebaseEntrant() {
    }

    public FirebaseEntrant(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getJoinedEvents() {
        return joinedEvents;
    }
    public boolean existsInJoinedEvents(FirebaseEvent event) {
        String eventID = event.getEventId();
        return joinedEvents.contains(eventID);
    }
    public void addToJoinedEvents(FirebaseEvent event) {
        String eventID = event.getEventId();
        if (!joinedEvents.contains(eventID)) {
            joinedEvents.add(eventID);
        }
    }
    public void removeFromJoinedEvents(FirebaseEvent event) {
        String eventID = event.getEventId();
        joinedEvents.remove(eventID);
    }

    public List<String> getWaitlistedEvents() {
        return waitlistedEvents;
    }
    public boolean existsInWaitlistedEvents(FirebaseEvent event) {
        String eventID = event.getEventId();
        return waitlistedEvents.contains(eventID);
    }
    public void addToWaitlistedEvents(FirebaseEvent event) {
        String eventID = event.getEventId();
        if (!waitlistedEvents.contains(eventID)) {
            waitlistedEvents.add(eventID);
        }
    }
    public void removeFromWaitlistedEvents(FirebaseEvent event) {
        String eventID = event.getEventId();
        waitlistedEvents.remove(eventID);
    }
}
