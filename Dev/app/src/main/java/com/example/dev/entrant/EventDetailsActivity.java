/**
 * EventDetailsActivity
 *
 * Shows event details.
 * Allows entrant to join or leave the waiting list.
 */

package com.example.dev.entrant;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import com.example.dev.utils.DeviceIdUtil;

import java.util.HashMap;
import java.util.Map;

public class EventDetailsActivity extends AppCompatActivity {

    TextView title, location, date;
    Button joinLeaveButton;
    boolean isJoined = false;

    String eventId;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_activity_event_details);

        MaterialToolbar toolbar = findViewById(R.id.eventDetailsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eventId = getIntent().getStringExtra("eventId");

        ImageView poster = findViewById(R.id.eventImage);
        poster.setImageResource(R.drawable.images);

        title = findViewById(R.id.title);
        location = findViewById(R.id.location);
        date = findViewById(R.id.date);

        title.setText(getIntent().getStringExtra("eventName"));
        location.setText("Location: " + getIntent().getStringExtra("location"));
        date.setText("Date: " + getIntent().getStringExtra("eventDate"));

        joinLeaveButton = findViewById(R.id.joinLeaveButton);

        loadCurrentJoinState();

        joinLeaveButton.setOnClickListener(v -> {
            if (isJoined) leaveWaitlist();
            else joinWaitlist();
        });
    }

    private void loadCurrentJoinState() {
        String deviceId = DeviceIdUtil.getDeviceId(this);

        db.collection("events").document(eventId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        var list = (java.util.List<Map<String,Object>>) doc.get("waitingList");

                        if (list != null) {
                            for (Map<String, Object> item : list) {
                                if (deviceId.equals(item.get("entrantId"))) {
                                    isJoined = true;
                                    break;
                                }
                            }
                        }
                        updateButtonUI();
                    }
                });
    }

    private void joinWaitlist() {
        String deviceId = DeviceIdUtil.getDeviceId(this);

        db.collection("entrants").document(deviceId).get()
                .addOnSuccessListener(entrantDoc -> {

                    if (!entrantDoc.exists()) return;

                    String name = entrantDoc.getString("name");
                    String email = entrantDoc.getString("email");

                    Map<String, Object> entrantData = new HashMap<>();
                    entrantData.put("entrantId", deviceId);
                    entrantData.put("name", name);
                    entrantData.put("email", email);

                    db.runTransaction(t -> {
                        t.update(db.collection("events").document(eventId),
                                "waitingList", FieldValue.arrayUnion(entrantData));

                        t.update(db.collection("entrants").document(deviceId),
                                "joinedEvents", FieldValue.arrayUnion(eventId));

                        return null;
                    }).addOnSuccessListener(a -> {
                        isJoined = true;
                        updateButtonUI();
                        Toast.makeText(this, "Joined waiting list!", Toast.LENGTH_SHORT).show();
                    });
                });
    }

    private void leaveWaitlist() {
        String deviceId = DeviceIdUtil.getDeviceId(this);

        db.collection("entrants").document(deviceId).get()
                .addOnSuccessListener(entrantDoc -> {

                    String name = entrantDoc.getString("name");
                    String email = entrantDoc.getString("email");

                    Map<String, Object> entrantData = new HashMap<>();
                    entrantData.put("entrantId", deviceId);
                    entrantData.put("name", name);
                    entrantData.put("email", email);

                    db.runTransaction(t -> {

                        // remove from event.waitingList
                        t.update(db.collection("events").document(eventId),
                                "waitingList", FieldValue.arrayRemove(entrantData));

                        // remove from entrant.joinedEvents
                        t.update(db.collection("entrants").document(deviceId),
                                "joinedEvents", FieldValue.arrayRemove(eventId));

                        return null;
                    }).addOnSuccessListener(a -> {
                        isJoined = false;
                        updateButtonUI();
                        Toast.makeText(this, "Left waiting list.", Toast.LENGTH_SHORT).show();
                    });
                });
    }

    private void updateButtonUI() {
        joinLeaveButton.setText(isJoined ? "Leave Waiting List" : "Join Waiting List");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
