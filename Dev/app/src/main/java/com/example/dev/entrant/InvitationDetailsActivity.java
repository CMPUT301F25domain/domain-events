/**
 * InvitationDetailsActivity
 *
 * Shows invitation results and lets entrant Accept / Decline.
 */

package com.example.dev.entrant;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.dev.R;
import com.example.dev.utils.DeviceIdUtil;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InvitationDetailsActivity extends AppCompatActivity {

    private static final String TAG = "InvitationDetails";

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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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

        acceptBtn.setOnClickListener(v -> handleInvitationResponse("accepted"));
        declineBtn.setOnClickListener(v -> handleInvitationResponse("declined"));
    }

    private void loadEventDetails() {
        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(this::processEventDocument);
    }

    private void processEventDocument(DocumentSnapshot doc) {
        if (!doc.exists()) return;

        String eventName = doc.getString("eventName");
        String eventDate = doc.getString("eventDate");
        String eventLocation = doc.getString("location");
        String posterUrl = doc.getString("posterUrl");
        String msg = "You have been selected for this event.";

        List<Map<String, Object>> waitingList = (List<Map<String, Object>>) doc.get("waitingList");
        int count = waitingList != null ? waitingList.size() : 0;

        // Find entrant's current status
        String currentStatus = "unknown";
        if (waitingList != null) {
            for (Map<String, Object> entrant : waitingList) {
                if (deviceId.equals(entrant.get("entrantId"))) {
                    currentStatus = (String) entrant.get("status");
                    break;
                }
            }
        }

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

        updateButtonState(currentStatus);
    }

    private void updateButtonState(String currentStatus) {
        String status = currentStatus != null ? currentStatus : "unknown";

        // Default to disabled for non-actionable or unknown states.
        acceptBtn.setEnabled(false);
        declineBtn.setEnabled(false);

        switch (status) {
            case "accepted":
                acceptBtn.setText("Already Accepted");
                break;
            case "deleted":
                acceptBtn.setText("You have been deleted");
                acceptBtn.setEnabled(false);
                declineBtn.setEnabled(false);
                break;
            case "declined":
                declineBtn.setText("Already Declined");
                break;
            case "invited":
            case "waitListed":
                acceptBtn.setEnabled(true);
                declineBtn.setEnabled(true);
                break;
            default:
                messageText.setText("Invitation revoked");
                break;
        }
    }

    private void handleInvitationResponse(String newStatus) {

        DocumentReference eventRef = db.collection("events").document(eventId);

        db.runTransaction((Transaction.Function<Void>) t -> {
            DocumentSnapshot snapshot = t.get(eventRef);

            List<Map<String, Object>> waitingList = (List<Map<String, Object>>) snapshot.get("waitingList");
            Map<String, Object> oldEntrantData = null;

            if (waitingList != null) {
                for (int i = 0; i < waitingList.size(); i++) {
                    Map<String, Object> entrant = waitingList.get(i);
                    // Find the entrant using the deviceId (entrantId)
                    if (deviceId.equals(entrant.get("entrantId"))) {
                        oldEntrantData = entrant;
                        break;
                    }
                }
            }

            if (oldEntrantData == null) {
                throw new FirebaseFirestoreException(
                        "Entrant not found; invitation may be revoked.",
                        FirebaseFirestoreException.Code.ABORTED
                );
            }

            String currentStatus = Objects.toString(oldEntrantData.get("status"), "unknown");
            if ("deleted".equals(currentStatus)) {
                throw new FirebaseFirestoreException(
                        "Invitation is no longer valid.",
                        FirebaseFirestoreException.Code.ABORTED
                );
            }
            Map<String, Object> newEntrantData = new java.util.HashMap<>(oldEntrantData);
            newEntrantData.put("status", newStatus);

            t.update(eventRef, "waitingList", FieldValue.arrayRemove(oldEntrantData));

            t.update(eventRef, "waitingList", FieldValue.arrayUnion(newEntrantData));

            if ("accepted".equals(newStatus)) {
                long currentCount = snapshot.getLong("attendingCount") != null ? snapshot.getLong("attendingCount") : 0;
                t.update(eventRef, "attendingCount", currentCount + 1);
            }

            return null;
        }).addOnSuccessListener(a -> {
            Toast.makeText(this, "Invitation " + newStatus + "!", Toast.LENGTH_SHORT).show();
            acceptBtn.setEnabled(false);
            declineBtn.setEnabled(false);

            if ("accepted".equals(newStatus)) {
                acceptBtn.setText("Accepted");
            } else {
                declineBtn.setText("Declined");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Transaction failed: " + e.getMessage());
            if (e instanceof FirebaseFirestoreException
                    && ((FirebaseFirestoreException) e).getCode() == FirebaseFirestoreException.Code.ABORTED) {
                Toast.makeText(this, "This invitation is no longer valid.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update invitation status.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}