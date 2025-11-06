package com.example.dev.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Placeholder upload screen that will be expanded with media handling in subsequent steps.
 */
public class UploadPosterActivity extends AppCompatActivity {

    private EventDraft eventDraft;
    private FirebaseFirestore firestore;
    private boolean isPublishing;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_poster);

        eventDraft = getIntent().getParcelableExtra(CreateEventActivity.EXTRA_EVENT_DRAFT);
        firestore = FirebaseFirestore.getInstance();

        TextView summaryView = findViewById(R.id.text_event_summary);
        if (eventDraft != null) {
            summaryView.setText(getString(R.string.upload_poster_summary_template,
                    valueOrPlaceholder(eventDraft.getEventName()),
                    valueOrPlaceholder(eventDraft.getLocation())));
        }

        Button backButton = findViewById(R.id.button_back_to_create);
        backButton.setOnClickListener(v -> returnToCreate());

        Button continueButton = findViewById(R.id.button_continue_to_publish);
        continueButton.setOnClickListener(v -> {
            if (!isPublishing) {
                publishEvent();
            }
        });
    }

    private void returnToCreate() {
        Intent result = new Intent();
        result.putExtra(CreateEventActivity.EXTRA_EVENT_DRAFT, eventDraft);
        setResult(RESULT_OK, result);
        finish();
    }

    private void publishEvent() {
        if (eventDraft == null) {
            Toast.makeText(this, R.string.upload_poster_missing_draft, Toast.LENGTH_LONG).show();
            return;
        }

        isPublishing = true;

        String eventName = eventDraft.getEventName();
        String location = eventDraft.getLocation();
        String eventDate = eventDraft.getEventDate();
        String eventTime = eventDraft.getEventTime();
        String eventStart = eventDraft.getRegistrationStart();
        String eventEnd = eventDraft.getRegistrationEnd();

        DocumentReference newEventRef = firestore.collection("events").document();
        String eventId = newEventRef.getId();
        FirebaseEvent newEvent = new FirebaseEvent(eventId, eventName, location, eventDate, eventTime, eventStart, eventEnd, 0);

        newEventRef.set(newEvent)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UploadPosterActivity.this,
                            getString(R.string.event_created_success, valueOrPlaceholder(eventName)),
                            Toast.LENGTH_LONG).show();
                    Intent result = new Intent();
                    result.putExtra(CreateEventActivity.EXTRA_EVENT_PUBLISHED, true);
                    setResult(RESULT_OK, result);
                    finish();
                })
                .addOnFailureListener(e -> {
                    isPublishing = false;
                    Toast.makeText(UploadPosterActivity.this, R.string.error_saving_event, Toast.LENGTH_LONG).show();
                });
    }

    private String valueOrPlaceholder(@Nullable String value) {
        return value == null || value.isEmpty() ? getString(R.string.upload_poster_unknown_value) : value;
    }
}
