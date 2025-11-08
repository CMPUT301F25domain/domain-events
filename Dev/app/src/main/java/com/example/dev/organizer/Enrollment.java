package com.example.dev.organizer;

public class Enrollment {
    public final String eventId;
    public final String entrantId;
    public final long enrolledAtMillis;

    public Enrollment(String eventId, String entrantId, long enrolledAtMillis) {
        this.eventId = eventId; this.entrantId = entrantId; this.enrolledAtMillis = enrolledAtMillis;
    }
}