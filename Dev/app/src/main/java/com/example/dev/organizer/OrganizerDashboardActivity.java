package com.example.dev.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dev.R;

import java.util.ArrayList;
import java.util.List;

public class OrganizerDashboardActivity extends AppCompatActivity implements EventAdapter.EventClickListener{

    private Button createEventbtn;
    private RecyclerView recyclerView;

    private EventAdapter adapter;

    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_organizer_dashboard);

        createEventbtn = findViewById(R.id.btn_create_event);
        recyclerView = findViewById(R.id.recycler_view_events);

        eventList = createMockEvents();
        adapter = new EventAdapter(eventList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        createEventbtn.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerDashboardActivity.this, CreateEventActivity.class);
            startActivity(intent);

        });
    }

    @Override
    public void onEventClick(String eventId){
       Intent detailIntent = new Intent(OrganizerDashboardActivity.this, EventDetailActivity.class);
       detailIntent.putExtra("Event_ID", eventId);
       startActivity(detailIntent);

    }

    private List<Event> createMockEvents(){
        List<Event> tempEvents = new ArrayList<>();

        tempEvents.add(new Event("EID_1", "Swimming Lessons", "Aquatic", "YMCA pool", "2d 5hrs", 25));

        tempEvents.add(new Event("EID_2", "Zumba Lesson", "Fitness", "Edmonton Gym", "3d 5hrs", 25));

        tempEvents.add(new Event("EID_3", "Soccer Lessons", "Sports", "YMCA field", "2d 5hrs", 25));

        return tempEvents;
    }


}
