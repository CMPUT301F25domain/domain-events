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
