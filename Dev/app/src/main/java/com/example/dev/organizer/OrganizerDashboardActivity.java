package com.example.dev.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dev.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.dev.firebaseobjects.EventAdapter;
import com.example.dev.firebaseobjects.FirebaseEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for the main dashboard -> Used by Event Organizers
 * Screen displays list of all events created by the organizer using RecyclerView
 * Retrieves data from the Firebase events collection
 * Navigates to event creation and event detail screen.
 */


public class OrganizerDashboardActivity extends AppCompatActivity implements EventAdapter.EventClickListener{

    private Button createEventbtn;
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<Event> eventList;
    private FirebaseFirestore database;
    private String organizerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_organizer_dashboard);

        database = FirebaseFirestore.getInstance();
        organizerId = getIntent().getStringExtra("organizerID");
        createEventbtn = findViewById(R.id.btn_create_event);
        recyclerView = findViewById(R.id.recycler_view_events);
        final int currentMenuItemId = R.id.navHome;

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(currentMenuItemId);

        eventList = new ArrayList<>();
        adapter = new EventAdapter(eventList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        createEventbtn.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerDashboardActivity.this, CreateEventActivity.class);
            intent.putExtra("organizerID", organizerId);
            startActivity(intent);
        });

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == currentMenuItemId) {
                return true;
            } else if (id == R.id.navHome) {
                Intent intent = new Intent(this, OrganizerDashboardActivity.class);
                intent.putExtra("organizerID", organizerId);
                startActivity(intent);
                return true;
            } else if (id == R.id.navProfile) {
                Intent intent = new Intent(this, OrganizerProfileActivity.class);
                intent.putExtra("organizerID", organizerId);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        getEventsFromFirebase();
    }

    /**
     * Retrieves event documents from Firebase events collection
     */

    private void getEventsFromFirebase(){
        database.collection("events").whereEqualTo("organizerId", organizerId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    eventList.clear();

                    for (QueryDocumentSnapshot document : task.getResult()){
                        FirebaseEvent fbEvent = document.toObject(FirebaseEvent.class);
                        Long maxLimitLong = document.getLong("waitlistLimit");
                        int eventCapacityInt = maxLimitLong != null ? maxLimitLong.intValue() : 0;

                        Long attendingCountLong = document.getLong("attendingCount");
                        int currentWaitListSize = attendingCountLong != null ? attendingCountLong.intValue() : 0;
                        String eventCapacityString = String.valueOf(eventCapacityInt);

                        Event displayTheNewEvent = new Event(
                                fbEvent.getEventId(), fbEvent.getEventName(),
                                "Default Category", fbEvent.getLocation(), fbEvent.getEventDate() + " at " +fbEvent.getEventTime(),
                                //(int) fbEvent.getAttendingCount(),
                                eventCapacityInt,
                                fbEvent.getPosterUrl()
                        );
                        eventList.add(displayTheNewEvent);
                    }

                    adapter.notifyDataSetChanged();

                    if (eventList.isEmpty()){
                        Toast.makeText(OrganizerDashboardActivity.this, "No Events found, Click Create Event to start.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    public void onEventClick(String eventId){
       Intent detailIntent = new Intent(OrganizerDashboardActivity.this, EventDetailActivity.class);
       detailIntent.putExtra("Event_ID", eventId);
       startActivity(detailIntent);
    }
}
