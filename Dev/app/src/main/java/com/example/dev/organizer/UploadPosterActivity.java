package com.example.dev.organizer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.text.TextUtils;
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


/**
 * Displays the poster upload screen, allowing organizers to pick an image preview that
 * travels with the draft through the rest of the publishing flow.
 */
public class UploadPosterActivity extends AppCompatActivity {

    private static final String STATE_POSTER_URI = "state_poster_uri";

    private EventDraft eventDraft;
    private boolean isPublishing;
    private Uri selectedPosterUri;

    private ActivityResultLauncher<PickVisualMediaRequest> pickPosterLauncher;
    private ActivityResultLauncher<Intent> publishEventLauncher;

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
                launchPublishFlow();
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

        publishEventLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null
                            && result.getData().getBooleanExtra(CreateEventActivity.EXTRA_EVENT_PUBLISHED, false)) {
                        setResult(RESULT_OK, result.getData());
                        finish();
                        return;
                    }

                    setPublishingState(false);

                    if (result.getResultCode() == RESULT_CANCELED && result.getData() != null) {
                        EventDraft returnedDraft = result.getData().getParcelableExtra(CreateEventActivity.EXTRA_EVENT_DRAFT);
                        if (returnedDraft != null) {
                            eventDraft = returnedDraft;
                        }
                        Uri returnedPosterUri = result.getData().getParcelableExtra(PublishEventActivity.EXTRA_POSTER_URI);
                        if (returnedPosterUri != null) {
                            selectedPosterUri = returnedPosterUri;
                            updatePosterPreview();
                        }
                        String errorMessage = result.getData().getStringExtra(PublishEventActivity.EXTRA_UPLOAD_ERROR_MESSAGE);
                        if (!TextUtils.isEmpty(errorMessage)) {
                            statusView.setText(errorMessage);
                            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                        } else {
                            statusView.setText(R.string.upload_poster_status_error);
                        }                    }
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
        launchPosterPicker();
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

    private void launchPublishFlow() {
        if (eventDraft == null) {
            Toast.makeText(this, R.string.upload_poster_missing_draft, Toast.LENGTH_LONG).show();
            return;
        }

        syncPosterUriToDraft();
        setPublishingState(true);

        Intent intent = new Intent(this, PublishEventActivity.class);
        intent.putExtra(CreateEventActivity.EXTRA_EVENT_DRAFT, eventDraft);
        if (selectedPosterUri != null) {
            intent.putExtra(PublishEventActivity.EXTRA_POSTER_URI, selectedPosterUri);
        }

        publishEventLauncher.launch(intent);
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
