package com.example.dev;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.dev.organizer.Entrant;
import com.example.dev.organizer.repo.impl.FirebaseWaitingListRepository;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class FirebaseWaitingListRepositoryTest {

    @Test
    public void parseEntrantUsesLatLngWhenLocationMissing() {
        FirebaseWaitingListRepository repo = new FirebaseWaitingListRepository();
        Map<String, Object> raw = baseEntrant();
        raw.put("latitude", 51.234567);
        raw.put("longitude", -114.123456);

        Entrant entrant = repo.parseEntrant(raw, "2024-04-20");

        assertNotNull(entrant);
        assertEquals("51.23457, -114.12346", entrant.location);
        assertEquals(1234L, entrant.joinedAtMillis);
    }

    @Test
    public void parseEntrantPrefersExistingLocationString() {
        FirebaseWaitingListRepository repo = new FirebaseWaitingListRepository();
        Map<String, Object> raw = baseEntrant();
        raw.put("location", "Calgary, AB");
        raw.put("latitude", 1.0);
        raw.put("longitude", 2.0);

        Entrant entrant = repo.parseEntrant(raw, "2024-04-20");

        assertEquals("Calgary, AB", entrant.location);
    }

    @Test
    public void parseEntrantSupportsGeoPointLocation() {
        FirebaseWaitingListRepository repo = new FirebaseWaitingListRepository();
        Map<String, Object> raw = baseEntrant();
        raw.put("location", new GeoPoint(35.0, -120.0));

        Entrant entrant = repo.parseEntrant(raw, "2024-04-20");

        assertEquals("35.00000, -120.00000", entrant.location);
    }

    private Map<String, Object> baseEntrant() {
        Map<String, Object> raw = new HashMap<>();
        raw.put("entrantId", "abc");
        raw.put("name", "Name");
        raw.put("email", "email@example.com");
        raw.put("joinedAtMillis", 1234L);
        return raw;
    }
}
