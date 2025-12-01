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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.dev.R;
import com.example.dev.utils.DeviceIdUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
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

    private static final String TAG = "EventDetailsActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    TextView title, locationView, date;
    TextView waitlistStatus;
    Button joinLeaveButton;
    ImageView poster;
    boolean isJoined = false;
    boolean isLocationRequired = false;

    private boolean isWaitListLimited = false;
    private int maxWaitListCapacity = 0;
    private int currentWaitList = 0;


    String eventId;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FusedLocationProviderClient fusedLocationClient;
    LocationCallback locationCallback;


    private static final int LOCATION_PERMISSION_REQUEST = 2001;

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

        poster = findViewById(R.id.eventImage);
        title = findViewById(R.id.title);
        locationView = findViewById(R.id.location);
        date = findViewById(R.id.date);
        waitlistStatus = findViewById(R.id.waitlistStatus);

        title.setText(getIntent().getStringExtra("eventName"));
        locationView.setText("Location: " + getIntent().getStringExtra("location"));
        date.setText("Date: " + getIntent().getStringExtra("eventDate"));

        // Load poster image from URL
        String posterUrl = getIntent().getStringExtra("posterUrl");
        loadPosterImage(posterUrl);

        joinLeaveButton = findViewById(R.id.joinLeaveButton);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        loadEventDetailsAndState();

        joinLeaveButton.setOnClickListener(v -> {
            joinLeaveButton.setEnabled(false);

            if (isJoined) {
                leaveWaitlist();
            } else {
                handleJoinClick();
            }
        });
    }

    private void loadPosterImage(String posterUrl) {
        if (posterUrl != null && !posterUrl.isEmpty() && !posterUrl.equals("null")) {
            Glide.with(this)
                    .load(posterUrl)
                    .placeholder(R.drawable.images)
                    .error(R.drawable.images)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(poster);
        } else {
            poster.setImageResource(R.drawable.images);
        }
    }

    private void loadEventDetailsAndState() {
        String deviceId = DeviceIdUtil.getDeviceId(this);

        joinLeaveButton.setEnabled(false);
        joinLeaveButton.setText("Loading...");

        db.collection("events").document(eventId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Boolean locReq = doc.getBoolean("locationRequired");
                        isLocationRequired = locReq != null && locReq;

                        Boolean limited = doc.getBoolean("waitlistLimited");
                        isWaitListLimited = limited != null && limited;

                        Long maxLimit = doc.getLong("waitlistLimit");
                        maxWaitListCapacity = maxLimit != null ? maxLimit.intValue() : 0;

                        List<Map<String, Object>> list = (List<Map<String, Object>>) doc.get("waitingList");
                        if (list != null) {
                            for (Map<String, Object> item : list) {
                                if (deviceId.equals(item.get("entrantId"))) {
                                    isJoined = true;
                                    break;
                                }
                            }
                        } else{
                            currentWaitList = 0;
                        }
                        updateWaitListStatusUI();
                        updateButtonUI();
                    } else {
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                        joinLeaveButton.setEnabled(true);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading event details", e);
                    Toast.makeText(this, "Error loading event details", Toast.LENGTH_SHORT).show();
                    joinLeaveButton.setEnabled(true);
                    joinLeaveButton.setText("Join Waiting List");
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

        Toast.makeText(this, "Getting your location...", Toast.LENGTH_SHORT).show();

        // Try cached location first
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        Log.d(TAG, "Using cached location: " + location.getLatitude() + ", " + location.getLongitude());
                        joinWaitlist(location.getLatitude(), location.getLongitude());
                    } else {
                        Log.d(TAG, "No cached location, requesting fresh location");
                        requestFreshLocation();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting last location", e);
                    requestFreshLocation();
                });
    }

    private void requestFreshLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            joinLeaveButton.setEnabled(true);
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .setMaxUpdates(1)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();
                if (location != null) {
                    Log.d(TAG, "Got fresh location: " + location.getLatitude() + ", " + location.getLongitude());
                    joinWaitlist(location.getLatitude(), location.getLongitude());
                } else {
                    Log.w(TAG, "Fresh location is still null");
                    showLocationFailureDialog();
                }

                fusedLocationClient.removeLocationUpdates(locationCallback);
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to request location updates", e);
                    showLocationFailureDialog();
                });
    }

    private void showLocationFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Location Required")
                .setMessage("Unable to get your location. Please ensure:\n\n" +
                        "• Location/GPS is turned ON\n" +
                        "• Location mode is set to 'High Accuracy'\n" +
                        "• You're not in airplane mode\n\n" +
                        "This event requires location data to join.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    joinWithLocation();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    joinLeaveButton.setEnabled(true);
                })
                .setNeutralButton("Open Settings", (dialog, which) -> {
                    startActivity(new android.content.Intent(
                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    joinLeaveButton.setEnabled(true);
                })
                .setCancelable(false)
                .show();
    }

    private void joinWaitlist(Double lat, Double lng) {
        String deviceId = DeviceIdUtil.getDeviceId(this);

        //Waitlist Capacity Check
        if(isWaitListLimited && currentWaitList >= maxWaitListCapacity){
            Toast.makeText(this, "The Wait List is Full. Cannot Join!", Toast.LENGTH_LONG).show();
            joinLeaveButton.setEnabled(true);
            updateButtonUI();
            return;
        }

        // If location is required but not provided, block the join
        if (isLocationRequired && (lat == null || lng == null)) {
            Toast.makeText(this, "Location is required for this event", Toast.LENGTH_LONG).show();
            joinLeaveButton.setEnabled(true);
            return;
        }

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
                    entrantData.put("status", "waitListed");

                    if (lat != null && lng != null) {
                        entrantData.put("latitude", lat);
                        entrantData.put("longitude", lng);
                        Log.d(TAG, "Adding location to waitlist entry: " + lat + ", " + lng);
                    }

                    db.runTransaction(t -> {
                                t.update(db.collection("events").document(eventId),
                                        "waitingList", FieldValue.arrayUnion(entrantData));

                                t.update(db.collection("entrants").document(deviceId),
                                        "joinedEvents", FieldValue.arrayUnion(eventId));

                                return null;
                            })
                            .addOnSuccessListener(a -> {
                                currentWaitList++;
                                isJoined = true;
                                updateWaitListStatusUI();
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
                                currentWaitList--;
                                isJoined = false;
                                updateWaitListStatusUI();
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
        if(!isJoined && isWaitListLimited && currentWaitList >= maxWaitListCapacity){
            joinLeaveButton.setText("Wait List is Full");
            joinLeaveButton.setEnabled(false);
            return;
        }
        joinLeaveButton.setText(isJoined ? "Leave Waiting List" : "Join Waiting List");
        joinLeaveButton.setEnabled(true);
    }

    private void updateWaitListStatusUI(){
        if (isWaitListLimited){
            int remaining = maxWaitListCapacity - currentWaitList;
            if (remaining <= 0){
                waitlistStatus.setText("Wait List Status: Full (MAX " + maxWaitListCapacity + ")");
                waitlistStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
            }else{
                waitlistStatus.setText("Wait List Status: " + remaining + " spots remaining.");
                waitlistStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light));

            }
        }else{
            waitlistStatus.setText("Wait List Status: Unlimited Capacity.");
            waitlistStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_light));

        }
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
    protected void onDestroy() {
        super.onDestroy();
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}