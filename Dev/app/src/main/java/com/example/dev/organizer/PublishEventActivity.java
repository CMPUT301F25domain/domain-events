package com.example.dev.organizer;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Handles persistence of a finalized event draft.
 */
public class PublishEventActivity extends AppCompatActivity {

    public static final String EXTRA_POSTER_URI = "com.example.dev.organizer.EXTRA_POSTER_URI";

    private static final String STATE_IS_PUBLISHING = "state_is_publishing";

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private EventDraft eventDraft;
    @Nullable
    private Uri posterUri;
    private boolean isPublishing;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
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

        DocumentReference newEventRef = firestore.collection("events").document();
        String eventId = newEventRef.getId();
        if (posterUri != null) {
            uploadPosterThenPublish(newEventRef, eventId);
        } else {
            writeEventDocument(newEventRef, eventId, eventDraft.getPosterUri());
        }
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
    private void uploadPosterThenPublish(@NonNull DocumentReference newEventRef, @NonNull String eventId) {
        StorageReference posterRef = buildPosterReference(eventId);
        posterRef.putFile(posterUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return posterRef.getDownloadUrl();
                })
                .addOnSuccessListener(downloadUri -> {
                    String posterUrl = downloadUri.toString();
                    eventDraft = eventDraft.withPosterUri(posterUrl);
                    writeEventDocument(newEventRef, eventId, posterUrl);
                })
                .addOnFailureListener(e -> onPosterUploadFailure());
    }

    private void writeEventDocument(@NonNull DocumentReference newEventRef,
                                    @NonNull String eventId,
                                    @Nullable String posterUrl) {
        String eventName = eventDraft.getEventName();
        String location = eventDraft.getLocation();
        String eventDate = eventDraft.getEventDate();
        String eventTime = eventDraft.getEventTime();
        String eventStart = eventDraft.getRegistrationStart();
        String eventEnd = eventDraft.getRegistrationEnd();
        String finalPosterUrl = !TextUtils.isEmpty(posterUrl) ? posterUrl : "";

        FirebaseEvent newEvent = new FirebaseEvent(eventId, eventName, location, eventDate, eventTime,
                eventStart, eventEnd, finalPosterUrl, 0);

        newEventRef.set(newEvent)
                .addOnSuccessListener(aVoid -> onPublishSuccess(eventName))
                .addOnFailureListener(e -> onPublishFailure());
    }

    private void onPosterUploadFailure() {
        isPublishing = false;
        Toast.makeText(this, R.string.error_uploading_poster, Toast.LENGTH_LONG).show();
        Intent result = new Intent();
        result.putExtra(CreateEventActivity.EXTRA_EVENT_DRAFT, eventDraft);
        if (posterUri != null) {
            result.putExtra(EXTRA_POSTER_URI, posterUri);
        }
        setResult(RESULT_CANCELED, result);
        finish();
    }

    @NonNull
    private StorageReference buildPosterReference(@NonNull String eventId) {
        String extension = resolvePosterExtension(posterUri);
        StringBuilder fileName = new StringBuilder(eventId)
                .append("/")
                .append(System.currentTimeMillis());
        if (!TextUtils.isEmpty(extension)) {
            fileName.append('.').append(extension);
        }
        return storage.getReference().child("event-posters").child(fileName.toString());
    }

    @NonNull
    private String resolvePosterExtension(@Nullable Uri uri) {
        if (uri == null) {
            return "";
        }
        String extension = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver resolver = getContentResolver();
            String type = resolver.getType(uri);
            if (type != null) {
                extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(type);
            }
        } else {
            String path = uri.getPath();
            if (path != null) {
                int dotIndex = path.lastIndexOf('.');
                if (dotIndex >= 0 && dotIndex < path.length() - 1) {
                    extension = path.substring(dotIndex + 1);
                }
            }
        }

        if (extension == null) {
            extension = "jpg";
        }
        return extension;
    }

    private String valueOrPlaceholder(@Nullable String value) {
        return value == null || value.isEmpty() ? getString(R.string.upload_poster_unknown_value) : value;
    }
}