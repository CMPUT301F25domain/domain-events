package com.example.dev.organizer;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.R;
import com.example.dev.firebaseobjects.FirebaseEvent;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
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
    private List<String> entrantDisplayList;
    private Map<String, String> entrantDisplayToIdMap;

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
        entrantDisplayList = new ArrayList<>();
        entrantDisplayToIdMap = new HashMap<>();

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
        for (Map<String, Object> entrant : waitingList) {
            String name = (String) entrant.get("name");
            String email = (String) entrant.get("email");
            String entrantId = (String) entrant.get("entrantId");
            String displayString = name + " (" + email + ")";
            entrantDisplayList.add(displayString);
            entrantDisplayToIdMap.put(displayString, entrantId);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, entrantDisplayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        entrantSpinner.setAdapter(adapter);
    }

    private void sendMessage() {
        String selectedEntrantDisplay = (String) entrantSpinner.getSelectedItem();
        if (selectedEntrantDisplay == null) {
            Toast.makeText(this, "Please select an entrant.", Toast.LENGTH_SHORT).show();
            return;
        }

        String entrantId = entrantDisplayToIdMap.get(selectedEntrantDisplay);
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
                }
            }
        });


        DocumentReference entrantRef = database.collection("entrants").document(entrantId);
        Map<String, Object> message = new HashMap<>();
        message.put("Message", messageText);
        entrantRef.update("lotteryMessage", FieldValue.arrayUnion(message))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DeleteEntrantActivity.this, "Message sent and entrant status updated.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DeleteEntrantActivity.this, "Failed to send message.", Toast.LENGTH_SHORT).show();
                });
    }
}
