/**
 * InvitationDetailsActivity
 *
 * Shows invitation results and lets entrant Accept / Decline.
 */

package com.example.dev.entrant;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.dev.R;
import com.example.dev.utils.DeviceIdUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class InvitationDetailsActivity extends AppCompatActivity {

    private TextView title, date, location, messageText, waitingCount;
    private ImageView poster;
    private Button acceptBtn, declineBtn;

    private String eventId;
    private String deviceId;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_details);

        eventId = getIntent().getStringExtra("eventId");
        deviceId = DeviceIdUtil.getDeviceId(this);

        setupToolbar();
        initViews();
        loadEventDetails();
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.invitationToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        title = findViewById(R.id.invTitle);
        date = findViewById(R.id.invDate);
        location = findViewById(R.id.invLocation);
        messageText = findViewById(R.id.invMessage);
        poster = findViewById(R.id.invImage);
        waitingCount = findViewById(R.id.invWaitingCount);

        acceptBtn = findViewById(R.id.invAcceptBtn);
        declineBtn = findViewById(R.id.invDeclineBtn);

        acceptBtn.setOnClickListener(v -> acceptInvitation());
        declineBtn.setOnClickListener(v -> declineInvitation());
    }

    private void loadEventDetails() {
        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    String eventName = doc.getString("eventName");
                    String eventDate = doc.getString("eventDate");
                    String eventLocation = doc.getString("location");
                    String posterUrl = doc.getString("posterUrl");

                    String msg = "You have been selected for this event.";

                    List<String> waitingList = (List<String>) doc.get("waitingList");
                    int count = waitingList != null ? waitingList.size() : 0;

                    title.setText(eventName);
                    date.setText("Date: " + eventDate);
                    location.setText("Location: " + eventLocation);
                    waitingCount.setText("Waiting List Count: " + count);
                    messageText.setText(msg);

                    if (posterUrl != null && !posterUrl.isEmpty()) {
                        Glide.with(this)
                                .load(posterUrl)
                                .placeholder(R.drawable.images)
                                .into(poster);
                    }

                    updateButtonState(doc);
                });
    }

    private void updateButtonState(com.google.firebase.firestore.DocumentSnapshot doc) {

        List<String> accepted = (List<String>) doc.get("acceptedList");
        List<String> declined = (List<String>) doc.get("declinedList");

        boolean alreadyAccepted = accepted != null && accepted.contains(deviceId);
        boolean alreadyDeclined = declined != null && declined.contains(deviceId);

        if (alreadyAccepted) {
            acceptBtn.setEnabled(false);
            declineBtn.setEnabled(false);
            acceptBtn.setText("Already Accepted");
        } else if (alreadyDeclined) {
            acceptBtn.setEnabled(false);
            declineBtn.setEnabled(false);
            declineBtn.setText("Already Declined");
        }
    }

    private void acceptInvitation() {
        String deviceId = DeviceIdUtil.getDeviceId(this);

        db.collection("entrants").document(deviceId).get()
                .addOnSuccessListener(entrantDoc -> {

                    if (!entrantDoc.exists()) return;

                    String name = entrantDoc.getString("name");
                    String email = entrantDoc.getString("email");

                    java.util.Map<String, Object> entrantData = new java.util.HashMap<>();
                    entrantData.put("entrantId", deviceId);
                    entrantData.put("name", name);
                    entrantData.put("email", email);

                    db.runTransaction(t -> {

                        var ref = db.collection("events").document(eventId);

                        t.update(ref, "waitingList", FieldValue.arrayRemove(entrantData));
                        t.update(ref, "acceptedList", FieldValue.arrayUnion(entrantData));
                        t.update(ref, "declinedList", FieldValue.arrayRemove(entrantData));

                        return null;

                    }).addOnSuccessListener(a -> {

                        Toast.makeText(this, "Invitation Accepted!", Toast.LENGTH_SHORT).show();
                        acceptBtn.setEnabled(false);
                        declineBtn.setEnabled(false);
                        acceptBtn.setText("Accepted");

                    }).addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to accept invitation.", Toast.LENGTH_SHORT).show()
                    );
                });
    }

    private void declineInvitation() {
        String deviceId = DeviceIdUtil.getDeviceId(this);

        db.collection("entrants").document(deviceId).get()
                .addOnSuccessListener(entrantDoc -> {

                    if (!entrantDoc.exists()) return;

                    String name = entrantDoc.getString("name");
                    String email = entrantDoc.getString("email");

                    java.util.Map<String, Object> entrantData = new java.util.HashMap<>();
                    entrantData.put("entrantId", deviceId);
                    entrantData.put("name", name);
                    entrantData.put("email", email);

                    db.runTransaction(t -> {

                        var ref = db.collection("events").document(eventId);

                        t.update(ref, "waitingList", FieldValue.arrayRemove(entrantData));
                        t.update(ref, "declinedList", FieldValue.arrayUnion(entrantData));
                        t.update(ref, "acceptedList", FieldValue.arrayRemove(entrantData));

                        return null;

                    }).addOnSuccessListener(a -> {

                        Toast.makeText(this, "Invitation Declined.", Toast.LENGTH_SHORT).show();
                        acceptBtn.setEnabled(false);
                        declineBtn.setEnabled(false);
                        declineBtn.setText("Declined");

                    }).addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to decline invitation.", Toast.LENGTH_SHORT).show()
                    );
                });
    }
}
