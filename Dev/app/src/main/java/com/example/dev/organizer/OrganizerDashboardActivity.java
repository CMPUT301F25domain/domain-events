package com.example.dev.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
//import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dev.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_organizer_dashboard);

        db = FirebaseFirestore.getInstance();

        createEventbtn = findViewById(R.id.btn_create_event);
        recyclerView = findViewById(R.id.recycler_view_events);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);

        eventList = new ArrayList<>();
        adapter = new EventAdapter(eventList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        createEventbtn.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerDashboardActivity.this, CreateEventActivity.class);
            startActivity(intent);

        });

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navHome) {
                getEventsFromFirebase();
                return true;
            } else if (id == R.id.navImages) {
//                Toast.makeText(this, "Images placeholder", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, OrganizerImagesActivity.class));

                return true;
            } else if (id == R.id.navProfile) {
                startActivity(new Intent(this, OrganizerProfileActivity.class));
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.navHome);
        }
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
        db.collection("events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    eventList.clear();

                    for (QueryDocumentSnapshot document : task.getResult()){
                        FirebaseEvent fbEvent = document.toObject(FirebaseEvent.class);

                        Event displayTheNewEvent = new Event(
                                fbEvent.getEventId(), fbEvent.getEventName(),
                                "Default Category", fbEvent.getLocation(), fbEvent.getEventDate() + " at " +fbEvent.getEventTime(),
                                (int) fbEvent.getAttendingCount()
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
