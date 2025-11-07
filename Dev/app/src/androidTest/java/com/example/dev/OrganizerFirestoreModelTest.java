package com.example.dev;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.dev.organizer.FirebaseEvent;

import org.junit.Test;

import java.util.Map;
import java.util.jar.Attributes;

public class OrganizerFirestoreModelTest {

   private String ID = "My_Test_01";
   private String eventName = "Cmput 301 seminar";
   private String location = "UofA";
   private String Date = "08/15/2025";
   private String Time = "18:00";
   private String regStart = "08/01/2025";
   private String regEnd = "08/08/2025";
   private int attendingCount = 50;

    /**
     * Tests constructer when geolocation is required
     */
   @Test
   public void test1_GeolocationRequirement() {
       FirebaseEvent event = new FirebaseEvent(ID, eventName, location, Date, Time, regStart, regEnd, attendingCount, true);

       assertEquals(ID, event.getEventId());
       assertEquals(eventName, event.getEventName());
       assertEquals(location, event.getLocation());
       assertEquals(attendingCount, event.getAttendingCount());

       assertTrue("Geoloction should be required", event.isLocationRequired());
   }

    @Test
    public void test2_GeolocationRequirement() {
        FirebaseEvent event = new FirebaseEvent(ID, eventName, location, Date, Time, regStart, regEnd, attendingCount, false);

        assertEquals(Date, event.getEventDate());
        assertEquals(Time, event.getEventTime());


        assertFalse("Geoloction should not be required", event.isLocationRequired());
    }



   }
