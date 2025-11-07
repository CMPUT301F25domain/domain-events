package com.example.dev;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class AdminFirestoreModelTest {

    @Test
    public void testFirestoreEventDataStructure() {

        // Mock Firestore event document
        Map<String, Object> event = new HashMap<>();
        event.put("eventName", "Zumba");
        event.put("location", "YMCA");
        event.put("eventStart", "12/01/2025");
        event.put("eventEnd", "12/15/2025");
        event.put("eventTime", "3:00");

        // Validate structure
        assertTrue(event.containsKey("eventName"));
        assertTrue(event.containsKey("eventStart"));
        assertTrue(event.containsKey("eventEnd"));
        assertTrue(event.containsKey("eventTime"));
        assertTrue(event.containsKey("location"));

        // Validate values
        assertEquals("Zumba", event.get("eventName"));
        assertEquals("YMCA", event.get("location"));
        assertEquals("12/01/2025", event.get("eventStart"));
    }
}
