package com.example.dev.entrant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class QRCodeScannerActivity extends AppCompatActivity {

    private Button scanQrBtn;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final ActivityResultLauncher<ScanOptions> qrScannerLauncher =
            registerForActivityResult(new ScanContract(), result -> {

                if (result.getContents() != null) {
                    String qrData = result.getContents();
                    Toast.makeText(this, "Scanned: " + qrData, Toast.LENGTH_SHORT).show();
                    Log.d("QR_SCAN", qrData);

                    if (qrData.startsWith("event:")) {
                        String eventId = qrData.substring("event:".length());
                        openEventDetailsWithFullExtras(eventId);
                    } else {
                        Toast.makeText(this, "Invalid Event QR Code", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant_qr_code_scanner);

        scanQrBtn = findViewById(R.id.btn_scan_QR);

        scanQrBtn.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setPrompt("Scan a QR Code");
            options.setBeepEnabled(true);
            options.setOrientationLocked(false);
            options.setBarcodeImageEnabled(true);
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);

            qrScannerLauncher.launch(options);
        });
    }

    private void openEventDetailsWithFullExtras(String eventId) {

        db.collection("events").document(eventId).get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String name = doc.getString("eventName");
                    String date = doc.getString("eventDate");
                    String location = doc.getString("location");
                    String posterUrl = doc.getString("posterUrl");

                    Intent intent = new Intent(this, EventDetailsActivity.class);
                    intent.putExtra("eventId", eventId);
                    intent.putExtra("eventName", name);
                    intent.putExtra("eventDate", date);
                    intent.putExtra("location", location);
                    intent.putExtra("posterUrl", posterUrl);

                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading event", Toast.LENGTH_SHORT).show();
                    Log.e("QR_SCAN", "Firestore error", e);
                });
    }
}
