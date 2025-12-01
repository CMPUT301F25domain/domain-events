package com.example.dev.organizer.repo.impl;

import com.example.dev.organizer.Enrollment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory enrollment store grouped by eventId. */
public class EnrollmentRepoMem {

    // eventId -> enrollments
    private final Map<String, List<Enrollment>> byEvent = new ConcurrentHashMap<>();

    private List<Enrollment> bucket(String eventId) {
        return byEvent.computeIfAbsent(eventId, k -> new ArrayList<>());
    }

    /** Add an enrollment record for an entrant. */
    public synchronized void enroll(String eventId, String entrantId) {
        // adjust constructor args to match your Enrollment class exactly
        Enrollment e = new Enrollment(eventId, entrantId, System.currentTimeMillis());
        bucket(eventId).add(e);
    }

    /** Return a snapshot of all enrollments for an event. */
    public synchronized List<Enrollment> list(String eventId) {
        return new ArrayList<>(bucket(eventId));
    }

    /** Optional helper: remove an enrollment for an entrant. */
    public synchronized void cancel(String eventId, String entrantId) {
        bucket(eventId).removeIf(en -> entrantId.equals(en.entrantId));
    }
}
