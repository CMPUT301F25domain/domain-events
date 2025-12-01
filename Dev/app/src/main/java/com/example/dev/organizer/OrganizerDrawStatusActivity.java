package com.example.dev.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dev.firebaseobjects.EntrantAdapter;
import com.example.dev.R;
import com.example.dev.firebaseobjects.FirebaseEvent;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrganizerDrawStatusActivity extends AppCompatActivity {
    private FirebaseFirestore database;
    private RecyclerView recyclerView;
    private Button btnReplaceEntrant, btnDeleteEntrant;
    private EntrantAdapter entrantAdapter;
    private List<Map<String, Object>> waitingList;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_draw_status);

        database = FirebaseFirestore.getInstance();
        eventId = getIntent().getStringExtra("Event_ID");

        recyclerView = findViewById(R.id.entrantRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        waitingList = new ArrayList<>();
        entrantAdapter = new EntrantAdapter(waitingList);
        recyclerView.setAdapter(entrantAdapter);

        fetchAndDisplayEntrants();

        btnReplaceEntrant = findViewById(R.id.btn_replace_entrant);
        btnDeleteEntrant = findViewById(R.id.btn_delete_entrant);

        btnReplaceEntrant.setOnClickListener(view -> {
            Intent intent = new Intent(OrganizerDrawStatusActivity.this, CurrentDrawActivity.class);
            intent.putExtra("Event_ID", eventId);
            startActivity(intent);
        });

        btnDeleteEntrant.setOnClickListener(view -> {
            Intent intent = new Intent(OrganizerDrawStatusActivity.this, DeleteEntrantActivity.class);
            intent.putExtra("Event_ID", eventId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAndDisplayEntrants();
    }

    private void fetchAndDisplayEntrants() {
        if (eventId == null) {
            Toast.makeText(this, "Event ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference eventRef = database.collection("events").document(eventId);
        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                FirebaseEvent event = documentSnapshot.toObject(FirebaseEvent.class);
                if (event != null && event.getWaitingList() != null) {
                    waitingList.clear();
                    waitingList.addAll(event.getWaitingList());
                    entrantAdapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch event details.", Toast.LENGTH_SHORT).show();
        });
    }
}