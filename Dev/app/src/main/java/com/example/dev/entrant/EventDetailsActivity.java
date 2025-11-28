/**
 * EventDetailsActivity
 *
 * Shows event details.
 * Allows entrant to join or leave the waiting list.
 */

package com.example.dev.entrant;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.dev.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import com.example.dev.utils.DeviceIdUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventDetailsActivity extends AppCompatActivity {

    TextView title, location, date;
    Button joinLeaveButton;
    boolean isJoined = false;

    String eventId;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST = 2001;

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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
        if (!hasLocationPermission()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            return;
        }
        fetchLocationAndJoin();
    }

    private void fetchLocationAndJoin() {
        String deviceId = DeviceIdUtil.getDeviceId(this);

        db.collection("entrants").document(deviceId).get()
                .addOnSuccessListener(entrantDoc -> {

                    if (!entrantDoc.exists()) return;

                    String name = entrantDoc.getString("name");
                    String email = entrantDoc.getString("email");
                    String profileLocation = entrantDoc.getString("location");

                    getLocationString(currentLocation -> {
                        Map<String, Object> entrantData = new HashMap<>();
                        entrantData.put("entrantId", deviceId);
                        entrantData.put("name", name);
                        entrantData.put("email", email);
                        entrantData.put("joinedAtMillis", System.currentTimeMillis());

                        if (currentLocation != null) {
                            entrantData.put("location", currentLocation);
                        } else if (profileLocation != null) {
                            entrantData.put("location", profileLocation);
                        }

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
                });
    }

    private void leaveWaitlist() {
        String deviceId = DeviceIdUtil.getDeviceId(this);

        db.collection("entrants").document(deviceId).get()
                .addOnSuccessListener(entrantDoc -> {

                    String name = entrantDoc.getString("name");
                    String email = entrantDoc.getString("email");

                    db.runTransaction(t -> {
                        var eventRef = db.collection("events").document(eventId);
                        var entrantRef = db.collection("entrants").document(deviceId);

                        DocumentSnapshot eventSnapshot = t.get(eventRef);
                        List<Map<String, Object>> waitingList = (List<Map<String, Object>>) eventSnapshot.get("waitingList");

                        if (waitingList != null) {
                            List<Map<String, Object>> updatedList = new ArrayList<>();
                            for (Map<String, Object> item : waitingList) {
                                if (item != null && deviceId.equals(item.get("entrantId"))) {
                                    continue;
                                }
                                updatedList.add(item);
                            }
                            t.update(eventRef, "waitingList", updatedList);
                        }

                        // remove from entrant.joinedEvents
                        t.update(entrantRef, "joinedEvents", FieldValue.arrayRemove(eventId));

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

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void getLocationString(Consumer<String> callback) {
        if (!hasLocationPermission()) {
            callback.accept(null);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> callback.accept(toLocationString(location)))
                .addOnFailureListener(e -> callback.accept(null));
    }

    private String toLocationString(Location location) {
        if (location == null) return null;
        return location.getLatitude() + "," + location.getLongitude();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            boolean granted = false;
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                    break;
                }
            }

            if (granted) {
                fetchLocationAndJoin();
            } else {
                Toast.makeText(this, "Location permission denied. Joining without location.", Toast.LENGTH_SHORT).show();
                fetchLocationAndJoin();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
