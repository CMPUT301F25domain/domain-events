/**
 * EntrantMessagesActivity
 *
 * This activity displays a placeholder "Messages" screen for the entrant user.
 * In the final version, this screen will list notifications such as:
 *  - Whether the entrant has been selected in a draw
 *  - Any updates from event organizers
 *
 * Current Behavior:
 *  - Shows a toolbar titled "Messages"
 *  - Allows returning to the previous screen via the back arrow
 *
 * Outstanding Issues:
 *  - Connect to Firestore to load actual messages
 *  - Replace static UI with a RecyclerView message list
 */
package com.example.dev.entrant;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.R;
import com.google.android.material.appbar.MaterialToolbar;

public class EntrantMessagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_activity_messages);

        MaterialToolbar toolbar = findViewById(R.id.messages_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Messages");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
