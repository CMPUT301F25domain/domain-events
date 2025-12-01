package com.example.dev.organizer.repo.impl;

import java.util.ArrayList;
import java.util.List;

import com.example.dev.repo.NotificationRepository;

public class NotificationRepoMem implements NotificationRepository {
    @Override public void log(String toUserId, String eventId, String message, String type) {
        InMemoryStore.i().notifLogByEvent.computeIfAbsent(eventId, k -> new ArrayList<>())
                .add(type + " -> " + toUserId + " : " + message);
    }
    @Override public List<String> messagesForEvent(String eventId) {
        return new ArrayList<>(InMemoryStore.i().notifLogByEvent.getOrDefault(eventId, new ArrayList<>()));
    }
}
