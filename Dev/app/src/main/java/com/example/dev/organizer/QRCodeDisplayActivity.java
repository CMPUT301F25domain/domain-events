package com.example.dev.organizer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRCodeDisplayActivity extends AppCompatActivity {
    private static final String TAG = "QRCodeDisplayActivity";

    private ImageView qrCodeImageView;
    private TextView eventIdTextView;
    private Button downloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_qr_code_display);

        qrCodeImageView = findViewById(R.id.IV_qr_code_display);
        eventIdTextView = findViewById(R.id.TV_event_id_display);
        downloadButton = findViewById(R.id.btn_download_qr);

        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        String eventId = getIntent().getStringExtra("Event_ID");

        downloadButton.setOnClickListener(v -> {
            if (qrCodeImageView.getDrawable() == null) {
                Toast.makeText(this, "QR code not available to save", Toast.LENGTH_SHORT).show();
                return;
            }

            qrCodeImageView.setDrawingCacheEnabled(true);
            Bitmap bitmap = ((android.graphics.drawable.BitmapDrawable) qrCodeImageView.getDrawable()).getBitmap();

            try {
                String savedURL = MediaStore.Images.Media.insertImage(
                        getContentResolver(),
                        bitmap,
                        "event_qr_" + System.currentTimeMillis(),
                        "QR code for event"
                );
                if (savedURL != null) {
                    Toast.makeText(this, "QR code saved to Photo Gallery", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to save QR code", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error saving QR code: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(this, "Event ID not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        eventIdTextView.setText("Event ID: " + eventId);
        generateQRCode(eventId);
    }

    private void generateQRCode(String eventId) {
        try {
            String qrData = "event:" + eventId;
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(qrData, BarcodeFormat.QR_CODE, 800, 800);
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.e(TAG, "QR code generation failed", e);
            Toast.makeText(this, "Failed to generate QR code.", Toast.LENGTH_SHORT).show();
        }
    }
}