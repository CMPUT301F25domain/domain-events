package com.example.dev.firebaseobjects;

import java.util.ArrayList;
import java.util.List;

public class FirebaseOrganizer {
    private String email;
    private String name;
    private String phone;
    private List<String> createdEvents = new ArrayList<>();

    public FirebaseOrganizer() {
    }

    public FirebaseOrganizer(String email, String name, String phone) {
        this.email = email;
        this.name = name;
        this.phone = phone;
    }

    public FirebaseOrganizer(String email, String name, String phone, List<String> createdEvents) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.createdEvents = createdEvents;
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

    public List<String> getCreatedEvents() {
        return createdEvents;
    }
    public boolean existsInCreatedEvents(FirebaseEvent event) {
        String eventID = event.getEventId();
        return createdEvents.contains(eventID);
    }
    public void addToCreatedEvents(FirebaseEvent event) {
        String eventID = event.getEventId();
        if (!createdEvents.contains(eventID)) {
            createdEvents.add(eventID);
        }
    }
    public void removeFromCreatedEvents(FirebaseEvent event) {
        String eventID = event.getEventId();
        createdEvents.remove(eventID);
    }
}
