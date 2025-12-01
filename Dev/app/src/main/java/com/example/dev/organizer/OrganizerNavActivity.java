package com.example.dev.organizer;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.dev.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class OrganizerNavActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_nav);
        View root = findViewById(R.id.organizer_root);
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

        BottomNavigationView bnv = findViewById(R.id.organizer_bottom_nav);

        // default tab
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
