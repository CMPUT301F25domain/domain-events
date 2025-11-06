package com.example.dev.organizer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.dev.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Displays the poster upload screen, allowing organizers to pick an image preview that
 * travels with the draft through the rest of the publishing flow.
 */
public class UploadPosterActivity extends AppCompatActivity {

    private static final String STATE_POSTER_URI = "state_poster_uri";

    private EventDraft eventDraft;
    private FirebaseFirestore firestore;
    private boolean isPublishing;
    private Uri selectedPosterUri;

    private ActivityResultLauncher<PickVisualMediaRequest> pickPosterLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    private ImageView posterPreview;
    private TextView previewPlaceholder;
    private TextView statusView;
    private ProgressBar publishProgress;
    private Button selectPosterButton;
    private Button backButton;
    private Button continueButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_poster);

        eventDraft = getIntent().getParcelableExtra(CreateEventActivity.EXTRA_EVENT_DRAFT);
        firestore = FirebaseFirestore.getInstance();

        registerLaunchers();
        bindViews();

        TextView summaryView = findViewById(R.id.text_event_summary);
        if (eventDraft != null) {
            summaryView.setText(getString(R.string.upload_poster_summary_template,
                    valueOrPlaceholder(eventDraft.getEventName()),
                    valueOrPlaceholder(eventDraft.getLocation())));
            if (eventDraft.getPosterUri() != null && savedInstanceState == null) {
                selectedPosterUri = Uri.parse(eventDraft.getPosterUri());
            }
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_POSTER_URI)) {
            selectedPosterUri = savedInstanceState.getParcelable(STATE_POSTER_URI);
        }

        updatePosterPreview();
        if (selectedPosterUri != null) {
            statusView.setText(R.string.upload_poster_status_ready);
        }

        backButton.setOnClickListener(v -> returnToCreate());

        continueButton.setOnClickListener(v -> {
            if (!isPublishing) {
                publishEvent();
            }
        });

        selectPosterButton.setOnClickListener(v -> {
            if (!isPublishing) {
                launchPosterSelection();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selectedPosterUri != null) {
            outState.putParcelable(STATE_POSTER_URI, selectedPosterUri);
        }
    }

    private void registerLaunchers() {
        pickPosterLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                onPosterSelected(uri);
            } else if (selectedPosterUri == null) {
                statusView.setText(R.string.upload_poster_status_selection_cleared);
                updatePosterPreview();
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted) {
                launchPosterPicker();
            } else {
                statusView.setText(R.string.upload_poster_status_permission_required);
            }
        });
    }

    private void bindViews() {
        posterPreview = findViewById(R.id.image_poster_preview);
        previewPlaceholder = findViewById(R.id.text_preview_placeholder);
        statusView = findViewById(R.id.text_upload_status);
        publishProgress = findViewById(R.id.progress_upload);
        selectPosterButton = findViewById(R.id.button_select_poster);
        backButton = findViewById(R.id.button_back_to_create);
        continueButton = findViewById(R.id.button_continue_to_publish);
    }

    private void launchPosterSelection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED) {
                launchPosterPicker();
            } else {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                launchPosterPicker();
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            launchPosterPicker();
        }
    }

    private void launchPosterPicker() {
        pickPosterLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void onPosterSelected(@NonNull Uri uri) {
        selectedPosterUri = uri;
        syncPosterUriToDraft();
        updatePosterPreview();
        statusView.setText(R.string.upload_poster_status_ready);
    }

    private void updatePosterPreview() {
        if (selectedPosterUri != null) {
            posterPreview.setImageURI(null);
            posterPreview.setImageURI(selectedPosterUri);
            previewPlaceholder.setVisibility(View.GONE);
        } else {
            posterPreview.setImageDrawable(null);
            previewPlaceholder.setVisibility(View.VISIBLE);
        }
    }

    private void syncPosterUriToDraft() {
        if (eventDraft != null) {
            eventDraft = eventDraft.withPosterUri(selectedPosterUri != null ? selectedPosterUri.toString() : null);
        }
    }

    private void returnToCreate() {
        Intent result = new Intent();
        syncPosterUriToDraft();
        if (eventDraft != null) {
            result.putExtra(CreateEventActivity.EXTRA_EVENT_DRAFT, eventDraft);
        }
        if (selectedPosterUri != null) {
            result.putExtra(CreateEventActivity.EXTRA_EVENT_POSTER_URI, selectedPosterUri.toString());
        }
        setResult(RESULT_OK, result);
        finish();
    }

    private void publishEvent() {
        if (eventDraft == null) {
            Toast.makeText(this, R.string.upload_poster_missing_draft, Toast.LENGTH_LONG).show();
            return;
        }

        syncPosterUriToDraft();
        setPublishingState(true);

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
                    syncPosterUriToDraft();
                    if (eventDraft != null) {
                        result.putExtra(CreateEventActivity.EXTRA_EVENT_DRAFT, eventDraft);
                    }
                    if (selectedPosterUri != null) {
                        result.putExtra(CreateEventActivity.EXTRA_EVENT_POSTER_URI, selectedPosterUri.toString());
                    }
                    result.putExtra(CreateEventActivity.EXTRA_EVENT_PUBLISHED, true);
                    setResult(RESULT_OK, result);
                    finish();
                })
                .addOnFailureListener(e -> {
                    setPublishingState(false);
                    statusView.setText(R.string.upload_poster_status_error);
                    Toast.makeText(UploadPosterActivity.this, R.string.error_saving_event, Toast.LENGTH_LONG).show();
                });
    }

    private void setPublishingState(boolean publishing) {
        isPublishing = publishing;
        publishProgress.setVisibility(publishing ? View.VISIBLE : View.GONE);
        selectPosterButton.setEnabled(!publishing);
        backButton.setEnabled(!publishing);
        continueButton.setEnabled(!publishing);
        if (publishing) {
            statusView.setText(R.string.upload_poster_status_publishing);
        }
    }

    private String valueOrPlaceholder(@Nullable String value) {
        return value == null || value.isEmpty() ? getString(R.string.upload_poster_unknown_value) : value;
    }
}
