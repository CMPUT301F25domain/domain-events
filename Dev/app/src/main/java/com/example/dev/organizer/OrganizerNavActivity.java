package com.example.dev.organizer;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dev.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class OrganizerNavActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_nav);

        BottomNavigationView bnv = findViewById(R.id.organizer_bottom_nav);

        // Default tab on first launch
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.organizer_container, new OrganizerWaitingListFragment())
                    .commit();
        }

        bnv.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_waiting_list) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.organizer_container, new OrganizerWaitingListFragment())
                        .commit();
                return true;
            } else if (id == R.id.nav_draw_status) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.organizer_container, new OrganizerDrawStatusFragment())
                        .commit();
                return true;
            } else if (id == R.id.nav_confirmed) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.organizer_container, new OrganizerConfirmedFragment())
                        .commit();
                return true;
            }
            return false;
        });
    }
}
