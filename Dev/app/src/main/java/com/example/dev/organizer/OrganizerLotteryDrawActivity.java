package com.example.dev.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class OrganizerLotteryDrawActivity extends AppCompatActivity {
    private Button defaultLotteryBtn, customLotteryBtn, startCustomLotteryBtn;
    private EditText participantNumberEditText;
    private TextView currentSizeTextView;
    private FirebaseFirestore database;
    private String eventId;
    private FirebaseEvent event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_lottery_draw);

        database = FirebaseFirestore.getInstance();
        eventId = getIntent().getStringExtra("Event_ID");

        defaultLotteryBtn = findViewById(R.id.btn_Default_Size_Start);
        customLotteryBtn = findViewById(R.id.btn_Custom_Size_Start);
        participantNumberEditText = findViewById(R.id.ET_participant_number);
        startCustomLotteryBtn = findViewById(R.id.btn_Start_Custom_Size);
        currentSizeTextView = findViewById(R.id.tv_current_size);

        if (eventId != null) {
            loadEventData();
        }

        defaultLotteryBtn.setOnClickListener(v -> {
            runLottery();
        });

        customLotteryBtn.setOnClickListener(v -> {
            participantNumberEditText.setVisibility(View.VISIBLE);
            startCustomLotteryBtn.setVisibility(View.VISIBLE);
        });

        startCustomLotteryBtn.setOnClickListener(v -> {
            String sizeStr = participantNumberEditText.getText().toString().trim();

            if (TextUtils.isEmpty(sizeStr)) {
                participantNumberEditText.setError("Number of participants is required");
                return;
            }

            try {
                int customSize = Integer.parseInt(sizeStr);
                if (customSize <= 0) {
                    participantNumberEditText.setError("Please enter a positive number");
                    return;
                }
                updateAttendingCountAndRunLottery(customSize);
            } catch (NumberFormatException e) {
                participantNumberEditText.setError("Please enter a valid number");
            }
        });
    }

    private void loadEventData() {
        DocumentReference eventRef = database.collection("events").document(eventId);
        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                event = documentSnapshot.toObject(FirebaseEvent.class);
                if (event != null) {
                    updateCurrentSizeDisplay();
                }
            }
        });
    }

    private void updateCurrentSizeDisplay() {
        if (event == null) return;

        int waitListCount = 0;
        List<Map<String, Object>> waitingList = event.getWaitingList();
        if (waitingList != null) {
            for (Map<String, Object> entrant : waitingList) {
                if ("waitListed".equals((String) entrant.get("status"))) {
                    waitListCount++;
                }
            }
        }

        currentSizeTextView.setText("Current Size: " + event.getAttendingCount() + " out of " + waitListCount + " people in wait list");
    }

    private void updateAttendingCountAndRunLottery(int customSize) {
        if (eventId == null) {
            Toast.makeText(this, "Event ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (event == null) {
            Toast.makeText(this, "Event details not loaded yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference eventRef = database.collection("events").document(eventId);

        eventRef.update("attendingCount", customSize)
                .addOnSuccessListener(aVoid -> {
                    event.setAttendingCount(customSize);
                    updateCurrentSizeDisplay();
                    Toast.makeText(this, "Event size updated to " + customSize, Toast.LENGTH_SHORT).show();

                    runLottery();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update event size.", Toast.LENGTH_SHORT).show();
                    Log.e("LOTTERY_ERROR", "Failed to update attendingCount", e);
                });
    }

    private void runLottery() {
        if (eventId == null) {
            Toast.makeText(this, "Event ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (event == null) {
            Toast.makeText(this, "Event details not loaded yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference eventRef = database.collection("events").document(eventId);

        List<Map<String, Object>> fullWaitingList = event.getWaitingList();
        List<Map<String, Object>> candidates = new ArrayList<>();

        for (Map<String, Object> entrant : fullWaitingList) {
            if ("waitListed".equals((String) entrant.get("status"))) {
                candidates.add(entrant);
            }
        }

        int drawSize = event.getAttendingCount();
        int numberOfWinners = Math.min(drawSize, candidates.size());

        Collections.shuffle(candidates);
        List<Map<String, Object>> winners = new ArrayList<>(candidates.subList(0, numberOfWinners));
        List<Map<String, Object>> losers = new ArrayList<>(candidates.subList(numberOfWinners, candidates.size()));

        for (Map<String, Object> winnerMap : winners) {
            String winnerId = (String) winnerMap.get("entrantId");
            if (winnerId == null) continue;

            for (Map<String, Object> originalEntry : fullWaitingList) {
                if (winnerId.equals(originalEntry.get("entrantId"))) {
                    originalEntry.put("status", "invited");
                    break;
                }
            }

            DocumentReference entrantRef = database.collection("entrants").document(winnerId);
            Map<String, Object> message = new HashMap<>();
            message.put("eventName", event.getEventName());
            message.put("eventDate", event.getEventDate());
            message.put("eventLocation", event.getLocation());
            message.put("eventId", event.getEventId());
            message.put("lotteryMessage", "You are selected for " + event.getEventName() + "!");
            message.put("lotteryStatus", true);

            entrantRef.update("Message", FieldValue.arrayUnion(message))
                    .addOnFailureListener(e -> Log.e("LOTTERY_ERROR", "Failed to send message", e));
        }

        for (Map<String, Object> loserMap : losers) {
            String loserId = (String) loserMap.get("entrantId");
            if (loserId == null) continue;

            DocumentReference entrantRef = database.collection("entrants").document(loserId);
            Map<String, Object> message = new HashMap<>();
            message.put("eventName", event.getEventName());
            message.put("eventDate", event.getEventDate());
            message.put("eventLocation", event.getLocation());
            message.put("eventId", event.getEventId());
            message.put("lotteryMessage", "You weren't selected for " + event.getEventName() + ", but stay in the wait list for spots to open. You might still be invited!");
            message.put("lotteryStatus", false);

            entrantRef.update("Message", FieldValue.arrayUnion(message))
                    .addOnFailureListener(e -> Log.e("LOTTERY_ERROR", "Failed to send message", e));
        }

        eventRef.update("waitingList", fullWaitingList)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, numberOfWinners + " entrant(s) invited.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OrganizerLotteryDrawActivity.this, OrganizerDrawStatusActivity.class);
                    intent.putExtra("Event_ID", eventId);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update entrant statuses.", Toast.LENGTH_SHORT).show();
                });
    }
}