package com.example.dev.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dev.R;
import com.example.dev.firebaseobjects.ConfirmAttendeesAdapter;
import com.example.dev.firebaseobjects.FirebaseEvent;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfirmAttendeesActivity extends AppCompatActivity {

    private RecyclerView attendeesRecyclerView;
    private Button btnDeleteEntrants;
    private FirebaseFirestore database;
    private String eventId;
    private ConfirmAttendeesAdapter adapter;
    private List<Map<String, Object>> acceptedEntrants = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_attendees);

        attendeesRecyclerView = findViewById(R.id.attendees_recycler_view);
        btnDeleteEntrants = findViewById(R.id.btn_delete_entrants);
        database = FirebaseFirestore.getInstance();
        eventId = getIntent().getStringExtra("Event_ID");

        setupRecyclerView();
        fetchAcceptedEntrants();

        btnDeleteEntrants.setOnClickListener(v -> {
            Intent intent = new Intent(ConfirmAttendeesActivity.this, DeleteEntrantActivity.class);
            intent.putExtra("Event_ID", eventId);
            intent.putExtra("STATUS_FILTER", "accepted");
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        adapter = new ConfirmAttendeesAdapter(acceptedEntrants);
        attendeesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        attendeesRecyclerView.setAdapter(adapter);
    }

    private void fetchAcceptedEntrants() {
        if (eventId == null) {
            Toast.makeText(this, "Event ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference eventRef = database.collection("events").document(eventId);
        eventRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Toast.makeText(this, "Error fetching event data.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                FirebaseEvent event = snapshot.toObject(FirebaseEvent.class);
                if (event != null && event.getWaitingList() != null) {
                    List<Map<String, Object>> filteredList = event.getWaitingList().stream()
                            .filter(entrant -> "accepted".equals(entrant.get("status")))
                            .collect(Collectors.toList());
                    acceptedEntrants.clear();
                    acceptedEntrants.addAll(filteredList);
                    adapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
