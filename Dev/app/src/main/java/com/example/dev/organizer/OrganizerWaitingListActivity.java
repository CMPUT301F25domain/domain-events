package com.example.dev.organizer;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class OrganizerWaitingListActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    // Simple host: no layout needed
                    .replace(android.R.id.content, new OrganizerWaitingListFragment())
                    .commit();
        }
    }
}
