package com.example.dev.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.R;
import com.example.dev.firebaseobjects.FirebaseEvent;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrentDrawActivity extends AppCompatActivity {

    private TextView tvDeclinedCount, tvAcceptedCount, tvDrawSize;
    private Button btnReplace, btnConfirmAttendees, btnViewList;

    private FirebaseFirestore database;
    private String eventId;
    private FirebaseEvent currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_current_draw);

        database = FirebaseFirestore.getInstance();
        eventId = getIntent().getStringExtra("Event_ID");

        tvDeclinedCount = findViewById(R.id.tv_declined_count);
        tvAcceptedCount = findViewById(R.id.tv_accepted_count);
        tvDrawSize = findViewById(R.id.tv_draw_size);
        btnReplace = findViewById(R.id.btn_replace);
        btnConfirmAttendees = findViewById(R.id.btn_confirm_attendees);
        btnViewList = findViewById(R.id.btn_view_list);

        fetchEventData();

        btnReplace.setOnClickListener(v -> {
            replaceLottery();
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
                currentEvent = snapshot.toObject(FirebaseEvent.class);
                if (currentEvent != null) {
                    updateDrawCounts();
                }
            } else {
                Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDrawCounts() {
        int declinedCount = 0;
        int acceptedCount = 0;
        int invitedCount = 0;

        if (currentEvent.getWaitingList() != null) {
            for (Map<String, Object> entrant : currentEvent.getWaitingList()) {
                String status = (String) entrant.get("status");
                if ("declined".equals(status)) {
                    declinedCount++;
                } else if ("accepted".equals(status)) {
                    acceptedCount++;
                } else if ("invited".equals(status)) {
                    invitedCount++;
                }
            }
        }

        int attendingCount = currentEvent.getAttendingCount();
        int totalInvitedAndAccepted = invitedCount + acceptedCount;
        int moreToInvite = attendingCount - totalInvitedAndAccepted;

        tvDrawSize.setText("Current Draw Size: " + attendingCount);
        tvDeclinedCount.setText(declinedCount + " entrant(s) declined invitation. " +
                Math.max(0, moreToInvite) + " more should be invited.");
        tvAcceptedCount.setText(acceptedCount + " entrant(s) accepted! " +
                invitedCount + " invite(s) pending.");
    }

    private void replaceLottery() {
        if (eventId == null || currentEvent == null) {
            Toast.makeText(this, "Event data is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Map<String, Object>> fullWaitingList = currentEvent.getWaitingList();
        if (fullWaitingList == null) {
            Toast.makeText(this, "No waiting list available.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Map<String, Object>> candidates = new ArrayList<>();
        int acceptedCount = 0;
        int invitedCount = 0;

        for (Map<String, Object> entrant : fullWaitingList) {
            String status = (String) entrant.get("status");
            if ("accepted".equals(status)) {
                acceptedCount++;
            } else if ("invited".equals(status)) {
                invitedCount++;
            } else if ("waitListed".equals(status)) {
                candidates.add(entrant);
            }
        }

        int moreToInvite = currentEvent.getAttendingCount() - (invitedCount + acceptedCount);

        if (moreToInvite <= 0) {
            Toast.makeText(this, "No replacements needed.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (candidates.isEmpty()) {
            Toast.makeText(this, "No waitlisted entrants available.", Toast.LENGTH_SHORT).show();
            return;
        }

        int numberOfWinners = Math.min(moreToInvite, candidates.size());
        Collections.shuffle(candidates);

        for (int i = 0; i < numberOfWinners; i++) {
            Map<String, Object> winner = candidates.get(i);
            String winnerId = (String) winner.get("entrantId");
            if (winnerId == null) continue;

            winner.put("status", "invited");

            DocumentReference entrantRef = database.collection("entrants").document(winnerId);
            Map<String, Object> message = new HashMap<>();
            message.put("eventName", currentEvent.getEventName());
            message.put("eventDate", currentEvent.getEventDate());
            message.put("eventLocation", currentEvent.getLocation());
            message.put("eventId", currentEvent.getEventId());
            message.put("lotteryMessage", "You are selected for " + currentEvent.getEventName() + "!");
            message.put("lotteryStatus", true);

            entrantRef.update("Message", FieldValue.arrayUnion(message))
                    .addOnFailureListener(e -> Log.e("LOTTERY_ERROR", "Failed to send message", e));
        }

        DocumentReference eventRef = database.collection("events").document(eventId);
        eventRef.update("waitingList", fullWaitingList)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, numberOfWinners + " entrant(s) invited.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CurrentDrawActivity.this, OrganizerDrawStatusActivity.class);
                    intent.putExtra("Event_ID", eventId);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update entrant statuses.", Toast.LENGTH_SHORT).show();
                });
    }
}