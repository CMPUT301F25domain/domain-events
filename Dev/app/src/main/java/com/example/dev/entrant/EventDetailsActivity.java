package com.example.dev.entrant;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.dev.R;
import com.example.dev.utils.DeviceIdUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDetailsActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailsActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    TextView title, locationView, date;
    Button joinLeaveButton;
    boolean isJoined = false;
    boolean isLocationRequired = false;

    String eventId;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_activity_event_details);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        MaterialToolbar toolbar = findViewById(R.id.eventDetailsToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        eventId = getIntent().getStringExtra("eventId");

        ImageView poster = findViewById(R.id.eventImage);
        poster.setImageResource(R.drawable.images);

        title = findViewById(R.id.title);
        locationView = findViewById(R.id.location);
        date = findViewById(R.id.date);

        title.setText(getIntent().getStringExtra("eventName"));
        locationView.setText("Location: " + getIntent().getStringExtra("location"));
        date.setText("Date: " + getIntent().getStringExtra("eventDate"));

        joinLeaveButton = findViewById(R.id.joinLeaveButton);

        loadEventDetailsAndState();

        joinLeaveButton.setOnClickListener(v -> {
            // Disable button to prevent double-clicks
            joinLeaveButton.setEnabled(false);

            if (isJoined) {
                leaveWaitlist();
            } else {
                handleJoinClick();
            }
        });
    }

    private void loadEventDetailsAndState() {
        String deviceId = DeviceIdUtil.getDeviceId(this);

        db.collection("events").document(eventId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Boolean locReq = doc.getBoolean("locationRequired");
                        isLocationRequired = locReq != null && locReq;

                        List<Map<String, Object>> list = (List<Map<String, Object>>) doc.get("waitingList");
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
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading event details", e);
                    Toast.makeText(this, "Error loading event details", Toast.LENGTH_SHORT).show();
                });
    }

    private void handleJoinClick() {
        if (isLocationRequired) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                joinWithLocation();
            }
        } else {
            joinWaitlist(null, null);
        }
    }

    private void joinWithLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            joinLeaveButton.setEnabled(true);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        joinWaitlist(location.getLatitude(), location.getLongitude());
                    } else {
                        // Location is null - join without coordinates
                        // You could also choose to block the user here
                        Log.w(TAG, "Location is null, joining without coordinates");
                        joinWaitlist(null, null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting location", e);
                    Toast.makeText(this, "Error getting location", Toast.LENGTH_SHORT).show();
                    joinLeaveButton.setEnabled(true);
                });
    }

    private void joinWaitlist(Double lat, Double lng) {
        String deviceId = DeviceIdUtil.getDeviceId(this);

        db.collection("entrants").document(deviceId).get()
                .addOnSuccessListener(entrantDoc -> {
                    if (!entrantDoc.exists()) {
                        Toast.makeText(this, "Entrant profile not found. Please create a profile first.",
                                Toast.LENGTH_LONG).show();
                        joinLeaveButton.setEnabled(true);
                        return;
                    }

                    String name = entrantDoc.getString("name");
                    String email = entrantDoc.getString("email");

                    Map<String, Object> entrantData = new HashMap<>();
                    entrantData.put("entrantId", deviceId);
                    entrantData.put("name", name);
                    entrantData.put("email", email);
                    entrantData.put("timestamp", System.currentTimeMillis());

                    // Only add location if both coordinates are provided
                    if (lat != null && lng != null) {
                        entrantData.put("latitude", lat);
                        entrantData.put("longitude", lng);
                    }

                    db.runTransaction(t -> {
                                t.update(db.collection("events").document(eventId),
                                        "waitingList", FieldValue.arrayUnion(entrantData));

                                t.update(db.collection("entrants").document(deviceId),
                                        "joinedEvents", FieldValue.arrayUnion(eventId));

                                return null;
                            })
                            .addOnSuccessListener(a -> {
                                isJoined = true;
                                updateButtonUI();
                                Toast.makeText(this, "Joined waiting list!", Toast.LENGTH_SHORT).show();
                                joinLeaveButton.setEnabled(true);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error joining waitlist", e);
                                Toast.makeText(this, "Failed to join: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                                joinLeaveButton.setEnabled(true);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching entrant document", e);
                    Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
                    joinLeaveButton.setEnabled(true);
                });
    }

    private void leaveWaitlist() {
        String deviceId = DeviceIdUtil.getDeviceId(this);

        db.collection("events").document(eventId).get()
                .addOnSuccessListener(eventDoc -> {
                    if (!eventDoc.exists()) {
                        joinLeaveButton.setEnabled(true);
                        return;
                    }

                    List<Map<String, Object>> waitingList =
                            (List<Map<String, Object>>) eventDoc.get("waitingList");
                    Map<String, Object> objectToRemove = null;

                    if (waitingList != null) {
                        for (Map<String, Object> entry : waitingList) {
                            if (deviceId.equals(entry.get("entrantId"))) {
                                objectToRemove = entry;
                                break;
                            }
                        }
                    }

                    if (objectToRemove == null) {
                        isJoined = false;
                        updateButtonUI();
                        joinLeaveButton.setEnabled(true);
                        Toast.makeText(this, "You are not on the waiting list",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final Map<String, Object> finalObjectToRemove = objectToRemove;

                    db.runTransaction(t -> {
                                t.update(db.collection("events").document(eventId),
                                        "waitingList", FieldValue.arrayRemove(finalObjectToRemove));

                                t.update(db.collection("entrants").document(deviceId),
                                        "joinedEvents", FieldValue.arrayRemove(eventId));

                                return null;
                            })
                            .addOnSuccessListener(a -> {
                                isJoined = false;
                                updateButtonUI();
                                Toast.makeText(this, "Left waiting list", Toast.LENGTH_SHORT).show();
                                joinLeaveButton.setEnabled(true);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error leaving waitlist", e);
                                Toast.makeText(this, "Failed to leave: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                                joinLeaveButton.setEnabled(true);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching event document", e);
                    Toast.makeText(this, "Error loading event", Toast.LENGTH_SHORT).show();
                    joinLeaveButton.setEnabled(true);
                });
    }

    private void updateButtonUI() {
        joinLeaveButton.setText(isJoined ? "Leave Waiting List" : "Join Waiting List");
        joinLeaveButton.setEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                joinWithLocation();
            } else {
                Toast.makeText(this, "Location permission is required to join this event.",
                        Toast.LENGTH_LONG).show();
                joinLeaveButton.setEnabled(true);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}