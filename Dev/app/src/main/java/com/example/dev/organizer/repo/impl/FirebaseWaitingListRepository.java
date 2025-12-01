package com.example.dev.organizer.repo.impl;

import androidx.annotation.NonNull;

import com.example.dev.organizer.Entrant;
import com.example.dev.repo.WaitingListRepository;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseWaitingListRepository implements WaitingListRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final Map<String, List<Entrant>> cache = new HashMap<>();

    @Override
    public void list(String eventId, Callback callback) {
        if (eventId == null || eventId.isEmpty()) {
            callback.onError(new IllegalArgumentException("eventId is required"));
            return;
        }

        // Return cached data immediately if present
        List<Entrant> cached = cache.get(eventId);
        if (cached != null && !cached.isEmpty()) {
            callback.onSuccess(new ArrayList<>(cached));
        }

        db.collection("events").document(eventId).get()
                .addOnSuccessListener(doc -> {
                    List<Map<String, Object>> waitingRaw = (List<Map<String, Object>>) doc.get("waitingList");
                    String eventDate = doc.getString("eventDate");
                    List<Entrant> entrants = new ArrayList<>();
                    if (waitingRaw != null) {
                        for (Map<String, Object> map : waitingRaw) {
                            if (map == null) continue;
                            entrants.add(parseEntrant(map, eventDate));
                        }
                    }
                    cache.put(eventId, entrants);
                    callback.onSuccess(new ArrayList<>(entrants));
                })
                .addOnFailureListener(callback::onError);
    }

    @Override
    public List<Entrant> cached(String eventId) {
        List<Entrant> cached = cache.get(eventId);
        if (cached == null) return Collections.emptyList();
        return new ArrayList<>(cached);
    }

    @Override
    public int cachedCount(String eventId) {
        return cache.getOrDefault(eventId, Collections.emptyList()).size();
    }

    @NonNull
    private Entrant parseEntrant(@NonNull Map<String, Object> map, String eventDate) {
        String id = safeString(map.get("entrantId"));
        String name = safeString(map.get("name"));
        String email = safeString(map.get("email"));
        String location = safeString(map.get("location"));
        long joinedAt = parseJoinedAt(map.get("joinedAtMillis"), map.get("timestamp"));
        return new Entrant(id, name, email, joinedAt, location, safeString(eventDate));
    }

    private static String safeString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private static long parseJoinedAt(Object joinedAtMillis, Object timestamp) {
        if (joinedAtMillis instanceof Number) {
            return ((Number) joinedAtMillis).longValue();
        }
        if (joinedAtMillis instanceof Timestamp) {
            return ((Timestamp) joinedAtMillis).toDate().getTime();
        }
        if (timestamp instanceof Number) {
            return ((Number) timestamp).longValue();
        }
        if (timestamp instanceof Timestamp) {
            return ((Timestamp) timestamp).toDate().getTime();
        }
        return 0L;
    }
}

