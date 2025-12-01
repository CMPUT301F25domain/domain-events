package com.example.dev.organizer;

public class Entrant {
    public final String id;
    public final String name;
    public final String email;
    public final long joinedAtMillis;
    public final String location;

    public Entrant(String id, String name, String email, long joinedAtMillis, String location) {
        this.id = id; this.name = name; this.email = email;
        this.joinedAtMillis = joinedAtMillis; this.location = location;
    }
    @Override public String toString() { return name + " <" + email + ">"; }
}
