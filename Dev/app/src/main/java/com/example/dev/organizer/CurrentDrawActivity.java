package com.example.dev.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.R;
import com.example.dev.firebaseobjects.FirebaseEvent;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class CurrentDrawActivity extends AppCompatActivity {

    private TextView tvDeclinedCount, tvAcceptedCount;
    private Button btnReplace, btnConfirmAttendees, btnViewList;

    private FirebaseFirestore database;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_current_draw);

        database = FirebaseFirestore.getInstance();
        eventId = getIntent().getStringExtra("Event_ID");

        tvDeclinedCount = findViewById(R.id.tv_declined_count);
        tvAcceptedCount = findViewById(R.id.tv_accepted_count);
        btnReplace = findViewById(R.id.btn_replace);
        btnConfirmAttendees = findViewById(R.id.btn_confirm_attendees);
        btnViewList = findViewById(R.id.btn_view_list);

        fetchEventData();

        btnReplace.setOnClickListener(v -> {
            // This shit doesn't do anything yet
            Toast.makeText(this, "Replace button clicked", Toast.LENGTH_SHORT).show();
        });

        btnConfirmAttendees.setOnClickListener(v -> {
            Intent intent = new Intent(CurrentDrawActivity.this, ConfirmAttendeesActivity.class);
            intent.putExtra("Event_ID", eventId);
            startActivity(intent);
        });

        btnViewList.setOnClickListener(v -> {
            finish();
        });
    }

    private void fetchEventData() {
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
                if (event != null) {
                    int declinedCount = 0;
                    int acceptedCount = 0;

                    if (event.getWaitingList() != null) {
                        for (Map<String, Object> entrant : event.getWaitingList()) {
                            String status = (String) entrant.get("status");
                            if ("declined".equals(status)) {
                                declinedCount++;
                            } else if ("accepted".equals(status)) {
                                acceptedCount++;
                            }
                        }
                    }

                    tvDeclinedCount.setText(declinedCount + " entrants Declined Invitation");
                    tvAcceptedCount.setText(acceptedCount + " Entrants Accepted!");
                }
            } else {
                Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
