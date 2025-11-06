package com.example.dev.organizer;

import android.content.Intent;
import android.graphics.Bitmap;     //Bitmap is needed for QR Code Generation
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.R;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.zxing.BarcodeFormat;                  //Zebra Crossing Barcode Scanner
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class EventDetailActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailActivity";

    private FirebaseFirestore db;

    private TextView textViewName;
    private TextView textViewLocation;
    private TextView textViewDateTime;
    private TextView textViewRegistration;


    private ProgressBar progressBar;
    private Button viewQRCodeBtn;
    private ImageView imageViewQRcode;

    private String eventId = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        initializeViews();
        db = FirebaseFirestore.getInstance();

        getEventIdFromDashboardIntent(getIntent());

        if (eventId != null) {
            getEventDetails(eventId);
        } else {
            Toast.makeText(this, "Error: Event ID not found.", Toast.LENGTH_LONG).show();
            finish();
        }

        viewQRCodeBtn.setOnClickListener(v -> {
            try {
                generateQRcode();
            } catch (WriterException e) {
                throw new RuntimeException(e);
            }
        });
    }




    private void initializeViews(){
        textViewName = findViewById(R.id.TV_detail_name);
        textViewLocation = findViewById(R.id.TV_detail_location);
        textViewDateTime = findViewById(R.id.TV_detail_date_time);
        textViewRegistration = findViewById(R.id.TV_detail_registration);
        progressBar = findViewById(R.id.progress_bar);
        viewQRCodeBtn = findViewById(R.id.btn_view_QR_code);
        imageViewQRcode = findViewById(R.id.IV_qr_code);

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

    private void generateQRcode() throws WriterException {
        if (eventId == null) {
            Toast.makeText(this, "Error: Event ID not found. Not my fault I think", Toast.LENGTH_LONG).show();
        }
        else {
            String qrData = "event:" + eventId;

            BarcodeEncoder qrEncoder = new BarcodeEncoder();
            Bitmap qrBitmap = qrEncoder.encodeBitmap(qrData, BarcodeFormat.QR_CODE, 800, 800);

            imageViewQRcode.setImageBitmap(qrBitmap);
            imageViewQRcode.setVisibility(View.VISIBLE);
        }
    }
}
