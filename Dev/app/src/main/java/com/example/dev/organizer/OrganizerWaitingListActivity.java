package com.example.dev.organizer;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class OrganizerWaitingListActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String eventId = getIntent() != null ? getIntent().getStringExtra("extra_event_id") : null;
        if (eventId == null) {
            Toast.makeText(this, "Missing event id", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            args.putString("extra_event_id", eventId);

            OrganizerWaitingListFragment fragment = new OrganizerWaitingListFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    // Simple host: no layout needed
                    .replace(android.R.id.content, fragment)
                    .commit();
        }
    }
}
