package com.example.dev.organizer.repo.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationRepoMem {

    // minimal in-memory buffer keyed by eventId for demo/testing
    private final Map<String, List<String>> byEvent = new HashMap<>();

    // ADD THIS METHOD (matches calls seen in fragments)
    public void log(String toUserId, String eventId, String message, String type) {
        String line = "[" + type + "] to=" + toUserId + " msg=" + message;
        synchronized (byEvent) {
            byEvent.computeIfAbsent(eventId, k -> new ArrayList<>()).add(line);
        }
    }

    // optional helper if you need to read back logs
    public List<String> messagesForEvent(String eventId) {
        synchronized (byEvent) {
            return new ArrayList<>(byEvent.getOrDefault(eventId, new ArrayList<>()));
        }
    }
}
