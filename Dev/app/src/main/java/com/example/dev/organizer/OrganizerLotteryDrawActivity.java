package com.example.dev.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private FirebaseFirestore database;
    private String eventId;

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

        defaultLotteryBtn.setOnClickListener(v -> {
            runLottery(-1);
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
                int size = Integer.parseInt(sizeStr);
                runLottery(size);
            } catch (NumberFormatException e) {
                participantNumberEditText.setError("Please enter a valid number");
            }
        });
    }

    private void runLottery(int customSize) {
        if (eventId == null) {
            Toast.makeText(this, "Event ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference eventRef = database.collection("events").document(eventId);

        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                FirebaseEvent event = documentSnapshot.toObject(FirebaseEvent.class);

                if (event != null) {
                    List<Map<String, Object>> fullWaitingList = event.getWaitingList();
                    List<Map<String, Object>> candidates = new ArrayList<>();

                    for (Map<String, Object> entrant : fullWaitingList) {
                        if ("waitListed".equals((String) entrant.get("status"))) {
                            candidates.add(entrant);
                        }
                    }

                    int drawSize = (customSize != -1) ? customSize : event.getAttendingCount();
                    int numberOfWinners = Math.min(drawSize, candidates.size());

                    Collections.shuffle(candidates);
                    List<Map<String, Object>> winners = new ArrayList<>(candidates.subList(0, numberOfWinners));

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
            } else {
                Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch event details.", Toast.LENGTH_SHORT).show();
        });
    }
}