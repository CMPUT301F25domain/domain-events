package com.example.entrantscreenactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;

import com.example.entrantscreenactivity.adapters.EventAdapter;
import com.example.entrantscreenactivity.models.Event;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.eventRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // TEMPORARY SAMPLE DATA
        eventList.add(new Event("Yoga Class", "Calgary", "Sept 20, 2025"));
        eventList.add(new Event("Piano Lessons", "Edmonton", "Sept 22, 2025"));
        eventList.add(new Event("Swimming Lessons", "Vancouver", "Oct 01, 2025"));

        recyclerView.setAdapter(new EventAdapter(this, eventList));
    }
}
