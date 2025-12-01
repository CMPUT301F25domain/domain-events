package com.example.dev.organizer;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.R;
import com.example.dev.utils.DeviceIdUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Organizer profile screen hosting OrganizerProfileFragment.
 */
public class OrganizerProfileActivity extends AppCompatActivity {
    private String organizerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_profile);

        organizerId = getIntent().getStringExtra("organizerID");
        if (organizerId == null) {
            organizerId = DeviceIdUtil.getDeviceId(this);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.organizer_profile_container, OrganizerProfileFragment.newInstance(organizerId))
                    .commit();
        }

        final int currentMenuItemId = R.id.navProfile;
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(currentMenuItemId);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == currentMenuItemId) {
                return true;
            } else if (id == R.id.navHome) {
                Intent intent = new Intent(this, OrganizerDashboardActivity.class);
                intent.putExtra("organizerID", organizerId);
                startActivity(intent);
                return true;
            } else if (id == R.id.navProfile) {
                return true;
            }
            return false;
        });
    }
}