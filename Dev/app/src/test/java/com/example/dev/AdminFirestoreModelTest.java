package com.example.dev;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
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
    @Test
    public void testFirestoreImageDataStructure() {

        // Mock Firestore image document
        Map<String, Object> image = new HashMap<>();
        image.put("eventName", "Rural FestEd");
        image.put("posterUrl", "https://domain-events-posters.s3.ca-west-1.amazonaws.com/posters/zaUSrOEyjkbhozavr0sx.jpg");
        image.put("posterUri", "https://domain-events-posters.s3.ca-west-1.amazonaws.com/posters/zaUSrOEyjkbhozavr0sx.jpg");

        // Validate structure
        assertTrue(image.containsKey("eventName"));
        assertTrue(image.containsKey("posterUrl"));
        assertTrue(image.containsKey("posterUri"));

        // Validate values
        assertEquals("Rural FestEd", image.get("eventName"));
        assertEquals("https://domain-events-posters.s3.ca-west-1.amazonaws.com/posters/zaUSrOEyjkbhozavr0sx.jpg", image.get("posterUrl"));
        assertEquals("https://domain-events-posters.s3.ca-west-1.amazonaws.com/posters/zaUSrOEyjkbhozavr0sx.jpg", image.get("posterUri"));
    }

    @Test
    public void testFirestoreNotificationDataStructure() {

        // Mock Firestore image document
        Map<String, Object> notification = new HashMap<>();
        notification.put("eventDate", "11/28/2025");
        notification.put("eventId", "jIj6A2xoteQJlcVneJ8p");
        notification.put("eventLocation", "Denver");
        notification.put("eventName", "Rural FestEd");
        notification.put("lotteryMessage", "You are selected for Rural FestEd!");
        notification.put("lotteryStatus", true);

        // Verify required fields exist
        assertTrue(notification.containsKey("eventDate"));
        assertTrue(notification.containsKey("eventId"));
        assertTrue(notification.containsKey("eventLocation"));
        assertTrue(notification.containsKey("eventName"));
        assertTrue(notification.containsKey("lotteryMessage"));
        assertTrue(notification.containsKey("lotteryStatus"));

        // Verify values
        assertEquals("11/28/2025", notification.get("eventDate"));
        assertEquals("jIj6A2xoteQJlcVneJ8p", notification.get("eventId"));
        assertEquals("Denver", notification.get("eventLocation"));
        assertEquals("Rural FestEd", notification.get("eventName"));
        assertEquals("You are selected for Rural FestEd!", notification.get("lotteryMessage"));
        assertEquals(true, notification.get("lotteryStatus"));
    }

    @Test
    public void testFirestoreProfileDataStructure() {

        Map<String, Object> profile = new HashMap<>();
        profile.put("email", "tanjil@gmail.com");
        profile.put("name", "tanjil");
        profile.put("phone", "67");

        // joined and waitlisted events as arrays
        profile.put("joinedEvents", List.of(
                "OPjEWKIlfwPjF7Acj7xV",
                "zaUSrOEyjkbozavrOsx"
        ));
        profile.put("waitlistedEvents", List.of());

        // Validate required fields
        assertTrue(profile.containsKey("email"));
        assertTrue(profile.containsKey("name"));
        assertTrue(profile.containsKey("phone"));
        assertTrue(profile.containsKey("joinedEvents"));
        assertTrue(profile.containsKey("waitlistedEvents"));

        assertEquals("tanjil@gmail.com", profile.get("email"));
        assertEquals("tanjil", profile.get("name"));
        assertEquals("67", profile.get("phone"));

        List<String> joinedEvents = (List<String>) profile.get("joinedEvents");
        assertEquals(2, joinedEvents.size());
        assertEquals("OPjEWKIlfwPjF7Acj7xV", joinedEvents.get(0));
        assertEquals("zaUSrOEyjkbozavrOsx", joinedEvents.get(1));

        List<String> waitlisted = (List<String>) profile.get("waitlistedEvents");
        assertTrue(waitlisted.isEmpty());

    }
}
