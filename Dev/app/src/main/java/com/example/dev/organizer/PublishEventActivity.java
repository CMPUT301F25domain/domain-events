package com.example.dev.organizer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Handles persistence of a finalized event draft.
 */
public class PublishEventActivity extends AppCompatActivity {

    public static final String EXTRA_POSTER_URI = "com.example.dev.organizer.EXTRA_POSTER_URI";

    private static final String STATE_IS_PUBLISHING = "state_is_publishing";

    private FirebaseFirestore firestore;
    private EventDraft eventDraft;
    @Nullable
    private Uri posterUri;
    private boolean isPublishing;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        eventDraft = getIntent().getParcelableExtra(CreateEventActivity.EXTRA_EVENT_DRAFT);
        posterUri = getIntent().getParcelableExtra(EXTRA_POSTER_URI);

        if (savedInstanceState != null) {
            isPublishing = savedInstanceState.getBoolean(STATE_IS_PUBLISHING, false);
        }

        if (eventDraft == null) {
            Toast.makeText(this, R.string.upload_poster_missing_draft, Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        if (!isPublishing) {
            publishEvent();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_IS_PUBLISHING, isPublishing);
    }

    private void publishEvent() {
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
                .addOnSuccessListener(aVoid -> onPublishSuccess(eventName))
                .addOnFailureListener(e -> onPublishFailure());
    }

    private void onPublishSuccess(@Nullable String eventName) {
        Toast.makeText(this,
                getString(R.string.event_created_success, valueOrPlaceholder(eventName)),
                Toast.LENGTH_LONG).show();
        Intent result = new Intent();
        result.putExtra(CreateEventActivity.EXTRA_EVENT_PUBLISHED, true);
        setResult(RESULT_OK, result);
        finish();
    }

    private void onPublishFailure() {
        isPublishing = false;
        Toast.makeText(this, R.string.error_saving_event, Toast.LENGTH_LONG).show();
        Intent result = new Intent();
        result.putExtra(CreateEventActivity.EXTRA_EVENT_DRAFT, eventDraft);
        if (posterUri != null) {
            result.putExtra(EXTRA_POSTER_URI, posterUri);
        }
        setResult(RESULT_CANCELED, result);
        finish();
    }

    private String valueOrPlaceholder(@Nullable String value) {
        return value == null || value.isEmpty() ? getString(R.string.upload_poster_unknown_value) : value;
    }
}