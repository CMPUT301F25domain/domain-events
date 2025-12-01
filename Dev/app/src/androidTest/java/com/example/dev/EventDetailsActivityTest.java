package com.example.dev;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.dev.entrant.EventDetailsActivity;
import com.example.dev.organizer.EventDetailActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class EventDetailsActivityTest {

    private static final String TEST_EVENT_NO_LOC = "testEventNoLoc";
    private static final String Test_Event_LOC = "testEventLoc";
    private static final String TEST_DEVICE_ID = "test_device_123";
    private static final String Emulator_HOST = "10.0.2.2";
    private static final int EMULATOR_PORT = 8080;

    private FirebaseFirestore db;
    private Context context;

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void setup(){
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        if (FirebaseApp.getApps(context).isEmpty()){
            FirebaseApp.initializeApp(context);
        }

        db = FirebaseFirestore.getInstance();

        db.useEmulator(Emulator_HOST, EMULATOR_PORT);
        FirebaseFirestoreSettings settings  = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build();
        db.setFirestoreSettings(settings);

        setupFirestoreData();
    }

    private void setupFirestoreData(){
        Map<String, Object> entrantData = new HashMap<>();
        entrantData.put("name", "Test Entrant");
        entrantData.put("email", "test@abc.com");
        db.collection("entrants").document(TEST_DEVICE_ID).set(entrantData).addOnFailureListener(e -> Log.e("Test", "Failed to set entrant data", e));

        Map<String,Object> eventNoLocData = new HashMap<>();
        eventNoLocData.put("eventName", "Event No Location");
        eventNoLocData.put("locationRequired", false);
        eventNoLocData.put("attendingCount" , 0L);
        eventNoLocData.put("waitingList", new ArrayList<>());
        db.collection("events").document(TEST_EVENT_NO_LOC).set(eventNoLocData).addOnFailureListener(e -> Log.e("Test", "Failed to set eventNoLoc data", e));

        Map<String,Object> eventLocData = new HashMap<>();
        eventLocData.put("eventName", "Event With Location");
        eventLocData.put("locationRequired", true);
        eventLocData.put("waitlistLimited", true);
        eventLocData.put("waitlistLimit", 1L);
        eventLocData.put("attendingCount" , 0L);
        eventLocData.put("waitingList", new ArrayList<>());
        db.collection("events").document(Test_Event_LOC).set(eventLocData).addOnFailureListener(e -> Log.e("Test", "Failed to set eventLoc data", e));

    }

    @After
    public void tearDown(){
        db.collection("events").document(TEST_EVENT_NO_LOC).delete();
        db.collection("events").document(Test_Event_LOC).delete();
        db.collection("entrants").document(TEST_DEVICE_ID).delete();
    }

    private Intent createTestIntent(String eventId, String eventName, boolean isLocationRequired){
        Intent intent = new Intent(getApplicationContext(), EventDetailsActivity.class);
        intent.putExtra("eventId", eventId);
        intent.putExtra("eventName", eventName);
        intent.putExtra("location", "Test Location");
        intent.putExtra("eventDate", "12/12/2025");
        intent.putExtra("posterUrl", "null");
        return intent;
    }

    /**
     * T1 -> Joined an event sucessfully without location requirement.
     */

    @Test
    public void testjoinNoLoc_Success(){
        Intent intent = createTestIntent(TEST_EVENT_NO_LOC, "Event No Location", false);
        try (ActivityScenario<EventDetailActivity> scenario = ActivityScenario.launch(intent)){
            onView(withId(R.id.waitlistStatus)).check(matches(withText("Wait List Status: Unlimited Capacity.")));
            onView(withId(R.id.joinLeaveButton)).check(matches(isEnabled()));
            onView(withId(R.id.joinLeaveButton)).perform(click());


        }

    }

}
