package com.example.dev.entrant;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dev.R;
import com.example.dev.services.MessageListenerService;
import com.example.dev.utils.DeviceIdUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EntrantBottomNavActivity extends AppCompatActivity {

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Notifications disabled. You can enable them in settings.", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant_bottom_nav);
        View root = findViewById(R.id.main);
        final int paddingLeft = root.getPaddingLeft();
        final int paddingTop = root.getPaddingTop();
        final int paddingRight = root.getPaddingRight();
        final int paddingBottom = root.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(paddingLeft, paddingTop + systemBars.top, paddingRight, paddingBottom + systemBars.bottom);
            return insets;
        });
        ViewCompat.requestApplyInsets(root);

        BottomNavigationView nav = findViewById(R.id.bottomNavigation);

        askNotificationPermission();

        Intent serviceIntent = new Intent(this, MessageListenerService.class);
        startService(serviceIntent);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new EntrantHomeFragment())
                    .commit();
        }

        String deviceId = DeviceIdUtil.getDeviceId(this);
        Log.d("DEVICE_ID", "Device ID = " + deviceId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("entrants").document(deviceId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("name", "");
                        data.put("email", "");
                        data.put("phone", "");
                        data.put("joinedEvents", new ArrayList<>());
                        db.collection("entrants").document(deviceId).set(data);
                    }
                });

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new EntrantHomeFragment())
                        .commit();
                return true;
            }

            if (id == R.id.nav_messages) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new EntrantMessagesFragment())
                        .commit();
                return true;
            }

            if (id == R.id.nav_profile) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new EntrantProfileFragment())
                        .commit();
                return true;
            }

            return false;
        });
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                Log.d("Notifications", "Permission already granted");
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}