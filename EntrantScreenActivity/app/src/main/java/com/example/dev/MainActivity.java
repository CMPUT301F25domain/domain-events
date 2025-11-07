package com.example.dev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.dev.adapters.EventAdapter;
import com.example.dev.models.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EventAdapter adapter;
    ArrayList<Event> eventList = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth.getInstance().signInAnonymously();

        recyclerView = findViewById(R.id.eventRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EventAdapter(this, eventList);
        recyclerView.setAdapter(adapter);

        loadEvents();
    }

    private void loadEvents() {
        db.collection("events").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("FIRESTORE_ERROR", error.getMessage());
                return;
            }

            eventList.clear();
            for (DocumentSnapshot doc : value.getDocuments()) {
                Event event = doc.toObject(Event.class);
                eventList.add(event);
            }

            adapter.notifyDataSetChanged();
        });
    }
}
