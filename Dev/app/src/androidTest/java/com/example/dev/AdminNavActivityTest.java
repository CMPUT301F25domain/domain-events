package com.example.dev;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.Espresso.onIdle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.dev.admin.AdminNavActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminNavActivityTest {

    @Rule
    public ActivityScenarioRule<AdminNavActivity> activityRule =
            new ActivityScenarioRule<>(AdminNavActivity.class);

    @Test
    public void testEventsFragmentShownOnLaunch() {
        onView(withId(R.id.eventTitle))
                .check(matches(isDisplayed()))
                .check(matches(withText("Events")));
    }

    @Test
    public void testBellIconOpensNotifications() {
        onView(withId(R.id.bellIcon)).perform(click());
        /*
            Source: GoogleCloud
            Title: "Espresso”
            Author/Entity: GoogleCloud
            License: CC BY 4.0
            URL: https://developer.android.com/reference/android/support/test/espresso/Espresso.html#onidle
        */
        Espresso.onIdle();
        onView(withId(R.id.notifTitle))
                .check(matches(isDisplayed()))
                .check(matches(withText("Notifications")));
    }



    @Test
    public void testBottomNavigationSwitchesFragments() {
        try (ActivityScenario<AdminNavActivity> scenario = ActivityScenario.launch(AdminNavActivity.class)) {

            // Go to Images
            onView(withId(R.id.navImages)).perform(click());
            Espresso.onIdle();
            onView(withId(R.id.fragment_container)).check(matches(isDisplayed()));

            // Go to Profile
            onView(withId(R.id.navProfile)).perform(click());
            Espresso.onIdle();
            onView(withId(R.id.fragment_container)).check(matches(isDisplayed()));
        }
    }



}
