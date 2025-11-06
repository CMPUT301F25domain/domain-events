package com.example.dev.organizer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

import com.example.dev.R;

/**
 * CreateEventActivity
 *
 * Screen for organizers to enter basic event fields (name, date/time, location,
 * registration window). On successful validation, it launches the poster-upload
 * flow (UploadPosterActivity) while passing the current form state as an EventDraft.
 *
 * Notes:
 * - This Activity is intentionally storage-agnostic. The commented Firestore code shows
 *   where persistence would occur later.
 * - We persist/restore the form using onSaveInstanceState() with a Parcelable EventDraft
 *   so transient state survives configuration changes (e.g., rotation).
 */
public class CreateEventActivity extends AppCompatActivity {
    static final String EXTRA_EVENT_DRAFT = "com.example.dev.organizer.EXTRA_EVENT_DRAFT";
    static final String EXTRA_EVENT_PUBLISHED = "com.example.dev.organizer.EXTRA_EVENT_PUBLISHED";
    static final String EXTRA_EVENT_POSTER_URI = "com.example.dev.organizer.EXTRA_EVENT_POSTER_URI";
    private EditText editTextEventName;
    private EditText editTextLocation;
    private EditText editTextEventTime;
    private EditText editTextEventDate;
    private EditText editTextStartDate;
    private EditText editTextEndDate;
//    private FirebaseFirestore db;

    private Button createButton;
    private ActivityResultLauncher<android.content.Intent> uploadPosterLauncher;
    @Nullable
    private String posterUri;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        initializeViews();
        setupUploadPosterLauncher();

        if (savedInstanceState != null) {
            EventDraft restoredDraft = savedInstanceState.getParcelable(EXTRA_EVENT_DRAFT);
            if (restoredDraft != null) {
                populateForm(restoredDraft);
            }
        }

        createButton.setOnClickListener(v -> {
            if (validateInput()) {
                launchUploadPoster();
            }
        });
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_EVENT_DRAFT, buildDraftFromInputs());
    }

    private void initializeViews(){
        editTextEventName = findViewById(R.id.ET_event_name);
        editTextEventDate = findViewById(R.id.ET_event_date);
        editTextEventTime = findViewById(R.id.ET_event_time);
        editTextLocation = findViewById(R.id.ET_location);
        editTextStartDate = findViewById(R.id.ET_registration_start);
        editTextEndDate = findViewById(R.id.ET_registration_end);
        createButton = findViewById(R.id.btn_upload_poster_and_event);
    }

    private void setupUploadPosterLauncher() {
        uploadPosterLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String returnedPosterUri = result.getData().getStringExtra(EXTRA_EVENT_POSTER_URI);
                        if (returnedPosterUri != null) {
                            posterUri = returnedPosterUri;
                        }
                        EventDraft returnedDraft = result.getData().getParcelableExtra(EXTRA_EVENT_DRAFT);
                        if (returnedDraft != null) {
                            populateForm(returnedDraft);
                        }
                    }
                }
        );
    }
    private boolean validateInput(){
        if (editTextEventName.getText().toString().trim().isEmpty() ||
                editTextLocation.getText().toString().trim().isEmpty() ||
                editTextEventDate.getText().toString().trim().isEmpty() ||
                editTextEventTime.getText().toString().trim().isEmpty()){
            String error = "Form Not Completed -> Please FIll all required fields.";
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            return false;

        }
        return true;
    }

    private void launchUploadPoster() {
        EventDraft draft = buildDraftFromInputs();
        android.content.Intent intent = new android.content.Intent(this, UploadPosterActivity.class);
        intent.putExtra(EXTRA_EVENT_DRAFT, draft);
        uploadPosterLauncher.launch(intent);
    }

    private EventDraft buildDraftFromInputs() {
        String eventName = editTextEventName.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String eventDate = editTextEventDate.getText().toString().trim();
        String eventTime = editTextEventTime.getText().toString().trim();
        String eventStart = editTextStartDate.getText().toString().trim();
        String eventEnd = editTextEndDate.getText().toString().trim();

//        DocumentReference newEventRef = db.collection("events").document();
//        String eventId = newEventRef.getId();
//
//        FirebaseEvent newEvent = new FirebaseEvent(eventId, eventName,location, eventDate, eventTime, eventStart, eventEnd,0);
//
//        newEventRef.set(newEvent).addOnSuccessListener(aVoid -> {
//            Toast.makeText(CreateEventActivity.this, "Event '" + eventName + "' created successfully!", Toast.LENGTH_LONG).show();
//            finish();
//        }).addOnFailureListener(e -> {
//            Toast.makeText(CreateEventActivity.this, "Error saving event!", Toast.LENGTH_LONG).show();
//
//        });
        return new EventDraft(eventName, location, eventDate, eventTime, eventStart, eventEnd, posterUri);
    }

    private void populateForm(EventDraft draft) {
        if (draft.getEventName() != null) {
            editTextEventName.setText(draft.getEventName());
        }
        if (draft.getLocation() != null) {
            editTextLocation.setText(draft.getLocation());
        }
        if (draft.getEventDate() != null) {
            editTextEventDate.setText(draft.getEventDate());
        }
        if (draft.getEventTime() != null) {
            editTextEventTime.setText(draft.getEventTime());
        }
        if (draft.getRegistrationStart() != null) {
            editTextStartDate.setText(draft.getRegistrationStart());
        }
        if (draft.getRegistrationEnd() != null) {
            editTextEndDate.setText(draft.getRegistrationEnd());
        }
        posterUri = draft.getPosterUri();
    }
}
