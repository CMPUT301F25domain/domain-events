package com.example.dev.organizer.repo.impl;

import androidx.annotation.NonNull;

import com.example.dev.organizer.Entrant;
import com.example.dev.repo.WaitingListRepository;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;


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
    public Entrant parseEntrant(@NonNull Map<String, Object> map, String eventDate) {
        String id = safeString(map.get("entrantId"));
        String name = safeString(map.get("name"));
        String email = safeString(map.get("email"));
        String location = resolveLocation(map);
        long joinedAt = parseJoinedAt(map.get("joinedAtMillis"), map.get("timestamp"));
        return new Entrant(id, name, email, joinedAt, location, safeString(eventDate));
    }

    private static String safeString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String resolveLocation(Map<String, Object> map) {
        Object locationValue = map.get("location");
        if (locationValue instanceof GeoPoint) {
            GeoPoint geo = (GeoPoint) locationValue;
            return formatLatLng(geo.getLatitude(), geo.getLongitude());
        }

        String location = safeString(locationValue).trim();
        if (!location.isEmpty()) {
            return location;
        }

        Double lat = toDouble(map.get("latitude"));
        Double lng = toDouble(map.get("longitude"));
        if (lat != null && lng != null) {
            return formatLatLng(lat, lng);
        }

        return "";
    }

    private static Double toDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble(((String) value).trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private static String formatLatLng(double lat, double lng) {
        return String.format(Locale.US, "%.5f, %.5f", lat, lng);
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

