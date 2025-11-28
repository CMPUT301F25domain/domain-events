package com.example.dev.organizer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.R;
import com.example.dev.firebaseobjects.FirebaseEvent;
import com.example.dev.firebaseobjects.FirebaseOrganizer;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Handles persistence of a finalized event draft.
 */
public class PublishEventActivity extends AppCompatActivity {

    private static final String TAG = "PublishEventActivity";

    // AWS S3 configuration
    private static final String S3_BUCKET_NAME = "domain-events-posters";
    // Use fromName so it works even if the enum constant isn't present
    private static final Regions S3_REGION = Regions.fromName("ca-west-1");
    private static final String COGNITO_POOL_ID =
            "ca-west-1:70a2ceb7-8665-43fc-88e2-bb68e662bd68";

    private AmazonS3Client s3Client;
    private TransferUtility transferUtility;

    public static final String EXTRA_POSTER_URI =
            "com.example.dev.organizer.EXTRA_POSTER_URI";
    public static final String EXTRA_UPLOAD_ERROR_MESSAGE =
            "com.example.dev.organizer.EXTRA_UPLOAD_ERROR_MESSAGE";

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
        // optional: if you have a layout for a spinner/progress
        // setContentView(R.layout.activity_publish_event);

        // Init Cognito + S3 client
        CognitoCachingCredentialsProvider credentialsProvider =
                new CognitoCachingCredentialsProvider(
                        getApplicationContext(),
                        COGNITO_POOL_ID,
                        S3_REGION
                );

        s3Client = new AmazonS3Client(credentialsProvider);
        transferUtility = TransferUtility.builder()
                .context(getApplicationContext())
                .s3Client(s3Client)
                .build();

        firestore = FirebaseFirestore.getInstance();
        eventDraft = getIntent().getParcelableExtra(CreateEventActivity.EXTRA_EVENT_DRAFT);
        posterUri = getIntent().getParcelableExtra(EXTRA_POSTER_URI);

        if (savedInstanceState != null) {
            isPublishing = savedInstanceState.getBoolean(STATE_IS_PUBLISHING, false);
        }

        if (eventDraft == null) {
            Toast.makeText(this,
                    R.string.upload_poster_missing_draft,
                    Toast.LENGTH_LONG).show();
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

    /**
     * Entry point: create a new event document id and either:
     *  - upload the poster to S3 (if we have a local Uri), then write the event
     *  - or just write the event using any existing URL stored in the draft
     */
    private void publishEvent() {
        isPublishing = true;

        DocumentReference newEventRef = firestore.collection("events").document();
        String eventId = newEventRef.getId();

        if (posterUri != null) {
            // New poster chosen for this draft → upload to S3
            uploadPosterThenPublish(newEventRef, eventId);
        } else {
            // No local Uri; fall back to whatever URL is already in the draft (may be null/empty)
            writeEventDocument(newEventRef, eventId, eventDraft.getPosterUri());
        }
    }

    private void onPublishSuccess(@Nullable String eventName) {
        Toast.makeText(
                this,
                getString(R.string.event_created_success,
                        valueOrPlaceholder(eventName)),
                Toast.LENGTH_LONG
        ).show();

        Intent result = new Intent();
        result.putExtra(CreateEventActivity.EXTRA_EVENT_PUBLISHED, true);
        setResult(RESULT_OK, result);
        finish();
    }

    private void onPublishFailure() {
        isPublishing = false;
        Toast.makeText(this,
                R.string.error_saving_event,
                Toast.LENGTH_LONG).show();

        Intent result = new Intent();
        result.putExtra(CreateEventActivity.EXTRA_EVENT_DRAFT, eventDraft);
        if (posterUri != null) {
            result.putExtra(EXTRA_POSTER_URI, posterUri);
        }
        setResult(RESULT_CANCELED, result);
        finish();
    }

    /**
     * Upload the local poster image to S3, then write the event with the resulting S3 URL.
     */
    private void uploadPosterThenPublish(DocumentReference newEventRef, String eventId) {
        Uri uriToUpload = posterUri;
        if (uriToUpload == null) {
            // Nothing to upload, just write event without a poster URL.
            writeEventDocument(newEventRef, eventId, null);
            return;
        }

        // Copy the content:// URI to a temporary File (TransferUtility requires a File)
        File tempFile;
        try {
            tempFile = copyUriToTempFile(uriToUpload, "poster-" + eventId + ".jpg");
        } catch (IOException e) {
            onPosterUploadFailure(e);
            return;
        }

        String key = "posters/" + eventId + ".jpg"; // S3 object key

        TransferObserver observer = transferUtility.upload(
                S3_BUCKET_NAME,
                key,
                tempFile
        );

        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    // Construct public URL (assuming bucket policy / ACL allows public read)
                    String url = buildS3PublicUrl(key);
                    Log.d(TAG, "S3 upload complete. URL = " + url);

                    writeEventDocument(newEventRef, eventId, url);
                } else if (state == TransferState.FAILED || state == TransferState.CANCELED) {
                    onPosterUploadFailure(
                            new RuntimeException("S3 upload failed, state = " + state)
                    );
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                // Hook a progress bar here if desired
            }

            @Override
            public void onError(int id, Exception ex) {
                onPosterUploadFailure(ex);
            }
        });
    }

    /**
     * Copy a content:// Uri into app cache directory so we can upload it as a File.
     */
    private File copyUriToTempFile(Uri uri, String fileName) throws IOException {
        File tempFile = new File(getCacheDir(), fileName);

        try (InputStream in = getContentResolver().openInputStream(uri);
             OutputStream out = new FileOutputStream(tempFile)) {

            byte[] buffer = new byte[8 * 1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        }

        return tempFile;
    }

    /**
     * Create and store the FirebaseEvent document in Firestore.
     *
     * posterUrl is:
     *  - a full S3 URL when an upload succeeded, or
     *  - an existing URL from the draft, or
     *  - null/empty when no poster is used.
     */
    private void writeEventDocument(@NonNull DocumentReference newEventRef,
                                    @NonNull String eventId,
                                    @Nullable String posterUrl) {
        String organizerId = eventDraft.getOrganizerId();
        String eventName = eventDraft.getEventName();
        String location = eventDraft.getLocation();
        String eventDate = eventDraft.getEventDate();
        String eventTime = eventDraft.getEventTime();
        String eventStart = eventDraft.getRegistrationStart();
        String eventEnd = eventDraft.getRegistrationEnd();
        String finalPosterUrl = !TextUtils.isEmpty(posterUrl) ? posterUrl : "";

        FirebaseEvent newEvent = new FirebaseEvent(
                eventId,
                eventName,
                location,
                eventDate,
                eventTime,
                eventStart,
                eventEnd,
                finalPosterUrl,
                0
        );

        newEventRef.set(newEvent)
                .addOnSuccessListener(aVoid -> onPublishSuccess(eventName))
                .addOnFailureListener(e -> onPublishFailure());
    }

    private void onPosterUploadFailure(@NonNull Exception exception) {
        Log.e(TAG, "Poster upload failed", exception);
        String errorMessage = exception.getMessage();

        isPublishing = false;
        Toast.makeText(this,
                R.string.error_uploading_poster,
                Toast.LENGTH_LONG).show();

        Intent result = new Intent();
        result.putExtra(CreateEventActivity.EXTRA_EVENT_DRAFT, eventDraft);
        if (posterUri != null) {
            result.putExtra(EXTRA_POSTER_URI, posterUri);
        }
        if (!TextUtils.isEmpty(errorMessage)) {
            result.putExtra(EXTRA_UPLOAD_ERROR_MESSAGE, errorMessage);
        }
        setResult(RESULT_CANCELED, result);
        finish();
    }

    private String valueOrPlaceholder(@Nullable String value) {
        return (value == null || value.isEmpty())
                ? getString(R.string.upload_poster_unknown_value)
                : value;
    }
    private String buildS3PublicUrl(String key) {
        return "https://" + S3_BUCKET_NAME
                + ".s3." + S3_REGION.getName()
                + ".amazonaws.com/" + key;
    }

}