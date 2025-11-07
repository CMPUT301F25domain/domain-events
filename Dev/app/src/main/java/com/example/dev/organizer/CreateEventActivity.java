package com.example.dev.organizer;

import android.media.metrics.BundleSession;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class CreateEventActivity extends AppCompatActivity {
    private EditText editTextEventName;
    private EditText editTextLocation;
    private EditText editTextEventTime;
    private EditText editTextEventDate;
    private EditText editTextStartDate;
    private EditText editTextEndDate;
    private FirebaseFirestore db;

    private Button createButton;
    private Switch locationSwitch;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        db = FirebaseFirestore.getInstance();

        initializeViews();

        createButton.setOnClickListener(v -> {
            if (validateInput()) {
                saveEventToFirebase();
            }
        });
    }

    private void initializeViews(){
        editTextEventName = findViewById(R.id.ET_event_name);
        editTextEventDate = findViewById(R.id.ET_event_date);
        editTextEventTime = findViewById(R.id.ET_event_time);
        editTextLocation = findViewById(R.id.ET_location);
        editTextStartDate = findViewById(R.id.ET_registration_start);
        editTextEndDate = findViewById(R.id.ET_registration_end);
        locationSwitch = findViewById(R.id.switch_geolocation);
        createButton = findViewById(R.id.btn_upload_poster_and_event);

    }

    private boolean validateInput(){
        if (editTextEventName.getText().toString().trim().isEmpty() || editTextLocation.getText().toString().trim().isEmpty() || editTextEventDate.getText().toString().trim().isEmpty() || editTextEventTime.getText().toString().trim().isEmpty()){
            String error = "Form Not Completed -> Please FIll all required fields.";
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            return false;

        }
        return true;
    }

    private void saveEventToFirebase(){
        String eventName = editTextEventName.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String eventDate = editTextEventDate.getText().toString().trim();
        String eventTime = editTextEventTime.getText().toString().trim();
        String eventStart = editTextStartDate.getText().toString().trim();
        String eventEnd = editTextEndDate.getText().toString().trim();

        boolean locationRequired = locationSwitch.isChecked();

        DocumentReference newEventRef = db.collection("events").document();
        String eventId = newEventRef.getId();

        FirebaseEvent newEvent = new FirebaseEvent(eventId, eventName,location, eventDate, eventTime, eventStart, eventEnd,0,locationRequired);

        newEventRef.set(newEvent).addOnSuccessListener(aVoid -> {
            Toast.makeText(CreateEventActivity.this, "Event '" + eventName + "' created successfully!", Toast.LENGTH_LONG).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(CreateEventActivity.this, "Error saving event!", Toast.LENGTH_LONG).show();

        });

    }
}
