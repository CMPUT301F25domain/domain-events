package com.example.dev.organizer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.R;
import com.example.dev.firebaseobjects.EntrantSpinnerAdapter;
import com.example.dev.firebaseobjects.FirebaseEvent;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeleteEntrantActivity extends AppCompatActivity {

    private Spinner entrantSpinner;
    private EditText messageEditText;
    private Button sendMessageButton;

    private FirebaseFirestore database;
    private String eventId;
    private String statusFilter;
    private List<Map<String, Object>> waitingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_delete_entrant);

        database = FirebaseFirestore.getInstance();
        eventId = getIntent().getStringExtra("Event_ID");
        statusFilter = getIntent().getStringExtra("STATUS_FILTER");

        entrantSpinner = findViewById(R.id.entrant_spinner);
        messageEditText = findViewById(R.id.message_edit_text);
        sendMessageButton = findViewById(R.id.send_message_button);

        waitingList = new ArrayList<>();

        fetchEntrants();

        sendMessageButton.setOnClickListener(v -> sendMessage());
    }

    private void fetchEntrants() {
        if (eventId == null) {
            Toast.makeText(this, "Event ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference eventRef = database.collection("events").document(eventId);
        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                FirebaseEvent event = documentSnapshot.toObject(FirebaseEvent.class);
                if (event != null && event.getWaitingList() != null) {
                    if (statusFilter != null && !statusFilter.isEmpty()) {
                        waitingList.addAll(event.getWaitingList().stream()
                                .filter(entrant -> statusFilter.equals(entrant.get("status")))
                                .collect(Collectors.toList()));
                    } else {
                        waitingList.addAll(event.getWaitingList());
                    }
                    populateSpinner();
                }
            } else {
                Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch event details.", Toast.LENGTH_SHORT).show();
        });
    }

    private void populateSpinner() {
        EntrantSpinnerAdapter adapter = new EntrantSpinnerAdapter(this, waitingList);
        entrantSpinner.setAdapter(adapter);
    }

    private void sendMessage() {
        Map<String, Object> selectedEntrant = (Map<String, Object>) entrantSpinner.getSelectedItem();
        if (selectedEntrant == null) {
            Toast.makeText(this, "Please select an entrant.", Toast.LENGTH_SHORT).show();
            return;
        }

        String entrantId = (String) selectedEntrant.get("entrantId");
        String messageText = messageEditText.getText().toString().trim();

        if (entrantId == null || messageText.isEmpty()) {
            Toast.makeText(this, "Please select an entrant and write a message.", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference eventRef = database.collection("events").document(eventId);
        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                FirebaseEvent event = documentSnapshot.toObject(FirebaseEvent.class);
                if(event != null) {
                    List<Map<String, Object>> updatedWaitingList = event.getWaitingList();
                    for (Map<String, Object> entrant : updatedWaitingList) {
                        if (entrantId.equals(entrant.get("entrantId"))) {
                            entrant.put("status", "deleted");
                            break;
                        }
                    }
                    eventRef.update("waitingList", updatedWaitingList);

                    DocumentReference entrantRef = database.collection("entrants").document(entrantId);

                    entrantRef.get().addOnSuccessListener(entrantDoc -> {
                        List<Map<String, Object>> messages = (List<Map<String, Object>>) entrantDoc.get("Message");
                        if (messages == null) {
                            messages = new ArrayList<>();
                        }

                        boolean messageUpdated = false;
                        for (Map<String, Object> msg : messages) {
                            if (eventId.equals(msg.get("eventId"))) {
                                msg.put("lotteryMessage", messageText);
                                msg.put("lotteryStatus", false);
                                messageUpdated = true;
                                break;
                            }
                        }

                        if (!messageUpdated) {
                            Map<String, Object> newMessage = new HashMap<>();
                            newMessage.put("eventName", event.getEventName());
                            newMessage.put("eventDate", event.getEventDate());
                            newMessage.put("eventLocation", event.getLocation());
                            newMessage.put("eventId", event.getEventId());
                            newMessage.put("lotteryMessage", messageText);
                            newMessage.put("lotteryStatus", false);
                            messages.add(newMessage);
                        }

                        entrantRef.update("Message", messages)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(DeleteEntrantActivity.this, "Message sent and entrant status updated.", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(DeleteEntrantActivity.this, "Failed to send message.", Toast.LENGTH_SHORT).show();
                                });
                    });
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch event details.", Toast.LENGTH_SHORT).show();
        });
    }
}