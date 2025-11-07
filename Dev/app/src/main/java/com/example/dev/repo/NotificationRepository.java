package com.example.dev.repo;

import java.util.List;

public interface NotificationRepository {
    void log(String toUserId, String eventId, String message, String type);
    List<String> messagesForEvent(String eventId);
}
