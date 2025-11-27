package com.example.dev.organizer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.dev.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

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
import java.util.HashMap;
import java.util.Map;

/**
 * Activity displays the full details of a single event.
 * Launched when a specific event is clicked on the organizer dashboard.
 */
public class EventDetailActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailActivity";

    // AWS S3 config – SAME values as in PublishEventActivity
    private static final String S3_BUCKET_NAME = "domain-events-posters";
    private static final Regions S3_REGION = Regions.fromName("ca-west-1");
    private static final String COGNITO_POOL_ID =
            "ca-west-1:70a2ceb7-8665-43fc-88e2-bb68e662bd68";

    private AmazonS3Client s3Client;
    private TransferUtility transferUtility;

    private FirebaseFirestore db;

    private TextView textViewName;
    private TextView textViewLocation;
    private TextView textViewDateTime;
    private TextView textViewRegistration;
    private ImageView posterImageView;
    private TextView posterPlaceholderView;
    private View posterContainer;

    private ProgressBar progressBar;
    private Button viewQRCodeBtn;
    private ImageView imageViewQRcode;
    private Button updatePosterBtn;
    private Button viewWaitingListBtn;

    private String eventId = null;

    @Nullable
    private String currentPosterUrl;
    private boolean isLoadingEvent;
    private boolean isPosterUpdating;

    private final ActivityResultLauncher<String> selectPosterLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onPosterSelected);

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Init AWS S3
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

        initializeViews();
        db = FirebaseFirestore.getInstance();

        getEventIdFromDashboardIntent(getIntent());

        // Open QR screen
        viewQRCodeBtn.setOnClickListener(v -> {
            if (eventId == null) {
                Toast.makeText(this, "Error: Event ID not found.", Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(EventDetailActivity.this, QRCodeDisplayActivity.class);
            intent.putExtra("Event_ID", eventId);
            startActivity(intent);
        });

        // Open Waiting List screen
        viewWaitingListBtn.setOnClickListener(v -> {
            if (eventId == null) {
                Toast.makeText(this, "Missing event id", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(this, OrganizerWaitingListActivity.class);
            i.putExtra("extra_event_id", eventId);   // keep literal key to avoid dependency
            startActivity(i);
        });

        if (eventId != null){
            getEventDetails(eventId);
        } else {
            Toast.makeText(this, "Error: Event ID not found.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews(){
        textViewName = findViewById(R.id.TV_detail_name);
        textViewLocation = findViewById(R.id.TV_detail_location);
        textViewDateTime = findViewById(R.id.TV_detail_date_time);
        textViewRegistration = findViewById(R.id.TV_detail_registration);
        posterContainer = findViewById(R.id.layout_event_poster_container);
        posterImageView = findViewById(R.id.image_event_poster);
        posterPlaceholderView = findViewById(R.id.text_event_poster_placeholder);
        progressBar = findViewById(R.id.progress_bar);
        viewQRCodeBtn = findViewById(R.id.btn_view_QR_code);
        updatePosterBtn = findViewById(R.id.button_update_poster);
        viewWaitingListBtn = findViewById(R.id.buttonViewWaitingList);

        setDetailsVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        updatePosterBtn.setOnClickListener(v -> showUpdatePosterDialog());
    }

    private void getEventIdFromDashboardIntent(Intent intent){
        if (intent != null && intent.hasExtra("Event_ID")){
            eventId = intent.getStringExtra("Event_ID");
            Log.d(TAG, "ID found in Extras: " + eventId);
        }
    }

    /** Fetch a single event document and display it. */
    private void getEventDetails(String id){
        isLoadingEvent = true;
        updateLoadingIndicator();
        DocumentReference eventRef = db.collection("events").document(id);
        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            isLoadingEvent = false;
            updateLoadingIndicator();

            if (documentSnapshot.exists()){
                FirebaseEvent event = documentSnapshot.toObject(FirebaseEvent.class);
                if (event != null){
                    displayEventData(event);
                    setDetailsVisibility(View.VISIBLE);
                }
            } else {
                currentPosterUrl = null;
                Toast.makeText(EventDetailActivity.this,
                        "Event not found in database.", Toast.LENGTH_LONG).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            isLoadingEvent = false;
            updateLoadingIndicator();
            Log.e(TAG, "Error getting event: ", e);
            Toast.makeText(EventDetailActivity.this,
                    "Error: could not load event", Toast.LENGTH_LONG).show();
            finish();
        });
    }

    /** Fill UI with event data. */
    private void displayEventData(FirebaseEvent event){
        textViewName.setText(event.getEventName());
        textViewLocation.setText("Location: " + event.getLocation());
        textViewDateTime.setText("Date: " + event.getEventDate() + " at " + event.getEventTime());
        String registrationInformation = String.format(
                "Registration: %s to %s | Attendees: %d",
                event.getEventStart(), event.getEventEnd(), event.getAttendingCount());
        textViewRegistration.setText(registrationInformation);

        currentPosterUrl = event.getPosterUrl();
        updatePosterDisplay(currentPosterUrl);
    }

    private void setDetailsVisibility(int visibility){
        textViewName.setVisibility(visibility);
        textViewLocation.setVisibility(visibility);
        textViewDateTime.setVisibility(visibility);
        textViewRegistration.setVisibility(visibility);
        posterContainer.setVisibility(visibility);
        viewQRCodeBtn.setVisibility(visibility);
        updatePosterBtn.setVisibility(visibility);
        viewWaitingListBtn.setVisibility(visibility);

        if (visibility == View.VISIBLE) {
            updatePosterDisplay(currentPosterUrl);
        } else {
            posterImageView.setVisibility(visibility);
            posterPlaceholderView.setVisibility(visibility);
        }
    }

    private void updatePosterDisplay(@Nullable String posterUrl) {
        if (posterContainer.getVisibility() != View.VISIBLE) return;

        if (TextUtils.isEmpty(posterUrl)) {
            Glide.with(this).clear(posterImageView);
            posterImageView.setImageDrawable(null);
            posterImageView.setVisibility(View.GONE);
            posterPlaceholderView.setVisibility(View.VISIBLE);
        } else {
            posterPlaceholderView.setVisibility(View.GONE);
            posterImageView.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(posterUrl)
                    .placeholder(R.drawable.bg_event_poster_placeholder)
                    .error(R.drawable.bg_event_poster_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(posterImageView);
        }
    }

    private void showUpdatePosterDialog() {
        if (eventId == null) {
            Toast.makeText(this, R.string.update_poster_missing_event, Toast.LENGTH_LONG).show();
            return;
        }
        if (isPosterUpdating) return;

        new AlertDialog.Builder(this)
                .setTitle(R.string.update_poster_dialog_title)
                .setMessage(R.string.update_poster_dialog_message)
                .setNegativeButton(R.string.update_poster_dialog_back,
                        (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.update_poster_dialog_confirm,
                        (dialog, which) -> launchPosterPicker())
                .show();
    }

    private void launchPosterPicker() {
        selectPosterLauncher.launch("image/*");
    }

    private void onPosterSelected(@Nullable Uri posterUri) {
        if (posterUri == null) return;
        if (eventId == null) {
            Toast.makeText(this, R.string.update_poster_missing_event,
                    Toast.LENGTH_LONG).show();
            return;
        }
        uploadPoster(posterUri);
    }

    /** Upload new poster to S3 and update Firestore with resulting URL. */
    private void uploadPoster(@Nullable Uri posterUri) {
        if (posterUri == null) return;

        setPosterUpdatingState(true);

        File tempFile;
        try {
            tempFile = copyUriToTempFile(posterUri, "poster-" + eventId + ".jpg");
        } catch (IOException e) {
            Log.e(TAG, "Failed to copy poster to temp file", e);
            setPosterUpdatingState(false);
            Toast.makeText(this, R.string.update_poster_failure, Toast.LENGTH_LONG).show();
            return;
        }

        String key = "posters/" + eventId + ".jpg";

        TransferObserver observer = transferUtility.upload(
                S3_BUCKET_NAME,
                key,
                tempFile
        );

        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    String url = s3Client.getResourceUrl(S3_BUCKET_NAME, key);
                    persistPosterUrl(url);
                } else if (state == TransferState.FAILED
                        || state == TransferState.CANCELED) {
                    Log.e(TAG, "S3 upload failed with state " + state);
                    setPosterUpdatingState(false);
                    Toast.makeText(EventDetailActivity.this,
                            R.string.update_poster_failure,
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                // optional: progress UI
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e(TAG, "S3 upload error", ex);
                setPosterUpdatingState(false);
                Toast.makeText(EventDetailActivity.this,
                        R.string.update_poster_failure,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void persistPosterUrl(String posterUrl) {
        if (eventId == null) {
            setPosterUpdatingState(false);
            return;
        }
        Map<String, Object> updates = new HashMap<>();
        updates.put("posterUrl", posterUrl);
        updates.put("posterUri", posterUrl);

        db.collection("events").document(eventId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    currentPosterUrl = posterUrl;
                    updatePosterDisplay(currentPosterUrl);
                    setPosterUpdatingState(false);
                    Toast.makeText(EventDetailActivity.this,
                            R.string.update_poster_success,
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update poster URL", e);
                    setPosterUpdatingState(false);
                    Toast.makeText(EventDetailActivity.this,
                            R.string.update_poster_failure,
                            Toast.LENGTH_LONG).show();
                });
    }

    private void setPosterUpdatingState(boolean updating) {
        isPosterUpdating = updating;
        updatePosterBtn.setEnabled(!updating);
        updatePosterBtn.setText(updating
                ? R.string.update_poster_uploading
                : R.string.update_poster);
        updateLoadingIndicator();
    }

    private void updateLoadingIndicator() {
        progressBar.setVisibility(
                (isLoadingEvent || isPosterUpdating) ? View.VISIBLE : View.GONE);
    }

    /** Copy a content:// Uri to a temp file so S3 TransferUtility can upload it. */
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
}
