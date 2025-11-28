package com.example.dev.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dev.firebaseobjects.EntrantAdapter;
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

public class OrganizerDrawStatusActivity extends AppCompatActivity {
    private FirebaseFirestore database;
    private RecyclerView recyclerView;
    private Button btnReplaceEntrant, btnDeleteEntrant;
    private EntrantAdapter entrantAdapter;
    private List<Map<String, Object>> waitingList;
    private String eventId;
    private int customSize = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_draw_status);

        database = FirebaseFirestore.getInstance();
        eventId = getIntent().getStringExtra("Event_ID");
        customSize = getIntent().getIntExtra("CUSTOM_SIZE", -1);

        recyclerView = findViewById(R.id.entrantRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        waitingList = new ArrayList<>();
        entrantAdapter = new EntrantAdapter(waitingList);
        recyclerView.setAdapter(entrantAdapter);

        runLotteryAndFetchInvitedEntrants();

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

    private void runLotteryAndFetchInvitedEntrants() {
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
                        if ("waitListed".equalsIgnoreCase((String) entrant.get("status"))) {
                            candidates.add(entrant);
                        }
                    }

                    int drawSize = (customSize != -1) ? customSize : event.getAttendingCount();
                    event.setAttendingCount(drawSize);
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
                                .addOnFailureListener(e -> Log.e("LOTTERY_ERROR", "Failed to send message to " + winnerMap.get("name"), e));
                    }

                    eventRef.update("waitingList", fullWaitingList)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, numberOfWinners + " entrant(s) invited.", Toast.LENGTH_SHORT).show();
                                displayEntrants(fullWaitingList);
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to update entrant statuses.", Toast.LENGTH_SHORT).show());

                } else {
                    Toast.makeText(this, "Event data is corrupt.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch event details.", Toast.LENGTH_SHORT).show();
        });
    }

    private void displayEntrants(List<Map<String, Object>> newWaitingList) {
        waitingList.clear();
        waitingList.addAll(newWaitingList);
        entrantAdapter.notifyDataSetChanged();
    }
}
