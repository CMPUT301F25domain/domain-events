package com.example.dev.views;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.example.dev.R;

public class EventDetailsActivity extends AppCompatActivity {

    TextView title, location, date;

    Button joinLeaveButton;
    boolean isJoined = false; // temporary state (later will come from Firebase)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        title = findViewById(R.id.title);
        location = findViewById(R.id.location);
        date = findViewById(R.id.date);

        title.setText(getIntent().getStringExtra("eventName"));
        location.setText("Location: " + getIntent().getStringExtra("location"));
        date.setText("Date: " + getIntent().getStringExtra("date"));



        joinLeaveButton = findViewById(R.id.joinLeaveButton);

        // Default state text
        updateButtonUI();

        joinLeaveButton.setOnClickListener(v -> {
            isJoined = !isJoined;   // toggle state
            updateButtonUI();
        });

    }

    private void updateButtonUI() {
        if (isJoined) {
            joinLeaveButton.setText("Leave Waiting List");
        } else {
            joinLeaveButton.setText("Join Waiting List");
        }
    }
}
