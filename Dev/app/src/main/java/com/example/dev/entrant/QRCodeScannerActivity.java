package com.example.dev.entrant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.R;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class QRCodeScannerActivity extends AppCompatActivity {
    private Button scanQrBtn;


    private final ActivityResultLauncher<ScanOptions> qrScannerLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if(result.getContents() != null) {
                    String qrData = result.getContents();
                    Toast.makeText(this, "Scanned: " + qrData, Toast.LENGTH_LONG).show();
                    Log.d("QR_SCAN", qrData);
                    if (qrData.startsWith("event:")) {
                        String eventId = qrData.substring("event:".length());

                        Intent intent = new Intent(this, EventDetailsActivity.class);
                        intent.putExtra("EVENT_ID", eventId);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(this, "Unknown Event", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
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
}
