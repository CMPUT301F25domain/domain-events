package com.example.dev.organizer.repo.impl;

import com.example.dev.organizer.Entrant;
import com.example.dev.organizer.Invitation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Shared in-memory store so all *RepoMem classes see the same data.
 * This is only for demo/testing; replace with real persistence later.
 */
final class InMemoryStore {

    // Entrant buckets keyed by eventId
    static final Map<String, List<Entrant>> waiting   = new ConcurrentHashMap<>();
    static final Map<String, List<Entrant>> invited   = new ConcurrentHashMap<>();
    static final Map<String, List<Entrant>> confirmed = new ConcurrentHashMap<>();
    static final Map<String, List<Entrant>> cancelled = new ConcurrentHashMap<>();

    // Invitation records keyed by eventId (used by InvitationRepoMem)
    static final Map<String, List<Invitation>> invitations = new ConcurrentHashMap<>();

    // Very simple notification log per event (used by NotificationRepoMem)
    static final Map<String, List<String>> eventNotifications = new ConcurrentHashMap<>();

    private InMemoryStore() {}

    /** Returns the mutable list bucket for the given eventId, creating it if needed. */
    static <T> List<T> bucket(Map<String, List<T>> map, String eventId) {
        return map.computeIfAbsent(eventId, k -> new ArrayList<>());
    }
}
