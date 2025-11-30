package com.example.dev.organizer;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dev.R;
import com.google.android.material.appbar.MaterialToolbar;

public class OrganizerWaitingListActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_waiting_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbarWaitingList);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.waiting_list_title);
            }
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

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
                    .replace(R.id.waiting_list_container, fragment)
                    .commit();
        }
    }
}