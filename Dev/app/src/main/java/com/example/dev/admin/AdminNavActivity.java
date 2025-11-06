package com.example.dev.admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dev.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * AdminNavActivity
 *
 * This activity serves as the main navigation area for the Admin section of the Event Lottery app.
 * It manages the bottom navigation bar and swaps between events, images, and profile screens.
 *
 * Purpose:
 * - Provides a bottom navigation bar for switching between different screens.
 * - Loads the Admin Events screen by default when the activity is launched.
 * - Handles navigation logic for Events, Images, and Profile sections.
 *
 * Design Pattern:
 * - Uses the Activity–Fragment pattern, where this activity serves as the host
 *   and each screen is a Fragment loaded dynamically.
 *
 * Outstanding Issues:
 * - Connect each fragment to Firebase to display and manage the actual event, image, and profile data.
 */

public class AdminNavActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_nav);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);

        // Load event screen initially
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AdminEventsFragment())
                    .commit();
        }

        // Handle navigation icon clicks
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            // Click home icon to go back to event screen
            if (id == R.id.navHome) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new AdminEventsFragment())
                        .commit();
                return true;
            }
            // Click images icon to go to image screen
            else if (id == R.id.navImages) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new AdminImageFragment())
                        .commit();
                return true;
            }
            //Click profile icon to go to profile screen
            else if (id == R.id.navProfile) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new AdminProfileFragment())
                        .commit();
                return true;
            }
            return false;
        });

    }
}
