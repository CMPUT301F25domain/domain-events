package com.example.dev.organizer;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Temporary placeholder screen for organizer images and media management.
 */
public class OrganizerImagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_images);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navHome) {
                startActivity(new Intent(this, OrganizerDashboardActivity.class));
                return true;
            } else if (id == R.id.navImages) {
                return true;
            } else if (id == R.id.navProfile) {
                startActivity(new Intent(this, OrganizerProfileActivity.class));
                return true;
            }
            return false;
        });

        bottomNavigation.setSelectedItemId(R.id.navImages);
    }
}
