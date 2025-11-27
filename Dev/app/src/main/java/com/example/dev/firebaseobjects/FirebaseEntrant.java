package com.example.dev.firebaseobjects;

import java.util.HashSet;
import java.util.Set;

public class FirebaseEntrant {
    private String email;
    private String name;
    private String phone;
    private Set<String> joinedEvents = new HashSet<>();
    private Set<String> waitlistedEvents = new HashSet<>();

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

    public Set<String> getJoinedEvents() {
        return joinedEvents;
    }
    public boolean existsInJoinedEvents(FirebaseEvent event) {
        String eventID = event.getEventId();
        return joinedEvents.contains(eventID);
    }
    public void addToJoinedEvents(FirebaseEvent event) {
        String eventID = event.getEventId();
        joinedEvents.add(eventID);
    }
    public void removeFromJoinedEvents(FirebaseEvent event) {
        String eventID = event.getEventId();
        joinedEvents.remove(eventID);
    }

    public Set<String> getWaitlistedEvents() {
        return waitlistedEvents;
    }
    public boolean existsInWaitlistedEvents(FirebaseEvent event) {
        String eventID = event.getEventId();
        return waitlistedEvents.contains(eventID);
    }
    public void addToWaitlistedEvents(FirebaseEvent event) {
        String eventID = event.getEventId();
        waitlistedEvents.add(eventID);
    }
    public void removeFromWaitlistedEvents(FirebaseEvent event) {
        String eventID = event.getEventId();
        waitlistedEvents.remove(eventID);
    }
}
