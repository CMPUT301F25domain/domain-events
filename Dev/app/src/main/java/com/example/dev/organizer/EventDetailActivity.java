package com.example.dev.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.R;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class EventDetailActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailActivity";

    private FirebaseFirestore db;

    private TextView textViewName;
    private TextView textViewLocation;
    private TextView textViewDateTime;
    private TextView textViewRegistration;

    private ProgressBar progressBar;
    private Button viewCodeBtn;
    private String eventId = null;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        initializeViews();
        db = FirebaseFirestore.getInstance();

        getEventIdFromDashboardIntent(getIntent());

        if (eventId != null){
            getEventDetails(eventId);
        }else{
            Toast.makeText(this, "Error: Event ID not found.", Toast.LENGTH_LONG).show();
            finish();
        }

//        viewCodeBtn.setOnClickListener(v -> {
//            Toast.makeText(this, "View QR code for ID: " + eventId, Toast.LENGTH_SHORT).show();
//        });
        }

     private void initializeViews(){
        textViewName = findViewById(R.id.TV_detail_name);
        textViewLocation = findViewById(R.id.TV_detail_location);
        textViewDateTime = findViewById(R.id.TV_detail_date_time);
        textViewRegistration = findViewById(R.id.TV_detail_registration);
        progressBar = findViewById(R.id.progress_bar);
        viewCodeBtn = findViewById(R.id.btn_create_event);

        setDetailsVisibility(View.GONE);

    }

    private void getEventIdFromDashboardIntent(Intent intent){
        if (intent.hasExtra("Event_ID")){
            eventId = intent.getStringExtra("Event_ID");
            Log.d(TAG, "ID found in Extras: " + eventId);
        }

    }

    private void getEventDetails(String id){
        progressBar.setVisibility(View.VISIBLE);
        DocumentReference eventRef = db.collection("events").document(id);
        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            progressBar.setVisibility(View.GONE);

            if (documentSnapshot.exists()){
                FirebaseEvent event = documentSnapshot.toObject(FirebaseEvent.class);

                if (event != null){
                    displayEventData(event);
                    setDetailsVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(EventDetailActivity.this, "Event not found in database.", Toast.LENGTH_LONG).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Log.e(TAG, "Error getting event: ", e);
            Toast.makeText(EventDetailActivity.this, "Error: could not load event", Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private void displayEventData(FirebaseEvent event){
        textViewName.setText(event.getEventName());
        textViewLocation.setText("Location: " + event.getLocation());
        textViewDateTime.setText("Date: " + event.getEventDate() + " at " + event.getEventTime());

        String registrationInformation = String.format("Registration: %s to %s | Attendees: %d", event.getEventStart(),event.getEventEnd(), event.getAttendingCount());
        textViewRegistration.setText(registrationInformation);
    }

    private void setDetailsVisibility(int visibility){
        textViewName.setVisibility(visibility);
        textViewLocation.setVisibility(visibility);
        textViewDateTime.setVisibility(visibility);
        textViewRegistration.setVisibility(visibility);
    }
}
