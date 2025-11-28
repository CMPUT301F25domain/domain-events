/**
 * EntrantBottomNavActivity
 *
 * Main activity for entrant users.
 * Hosts 3 fragments inside a bottom navigation layout:
 *  - EntrantHomeFragment
 *  - EntrantMessagesFragment
 *  - EntrantProfileFragment
 *
 * Responsibilities:
 *  - Initialize bottom navigation
 *  - Load default fragment (Home)
 *  - Auto-create entrant Firestore profile using device ID
 *  - Switch fragments based on selected navigation item
 *
 * Acts as the core container for all Entrant screens.
 */

package com.example.dev.entrant;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.R;
import com.example.dev.utils.DeviceIdUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EntrantBottomNavActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant_bottom_nav);

        BottomNavigationView nav = findViewById(R.id.bottomNavigation);

        // Load Home fragment by default
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
}
