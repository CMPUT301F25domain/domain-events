package com.example.dev.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class OrganizerLotteryDrawActivity extends AppCompatActivity {
    private Button defaultLotteryBtn, customLotteryBtn, startCustomLotteryBtn;
    private EditText participantNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_lottery_draw);

        defaultLotteryBtn = findViewById(R.id.btn_Default_Size_Start);
        customLotteryBtn = findViewById(R.id.btn_Custom_Size_Start);
        participantNumberEditText = findViewById(R.id.ET_participant_number);
        startCustomLotteryBtn = findViewById(R.id.btn_Start_Custom_Size);

        String eventId = getIntent().getStringExtra("Event_ID");

        defaultLotteryBtn.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerLotteryDrawActivity.this, OrganizerDrawStatusActivity.class);
            intent.putExtra("Event_ID", eventId);
            startActivity(intent);
        });

        customLotteryBtn.setOnClickListener(v -> {
            participantNumberEditText.setVisibility(View.VISIBLE);
            startCustomLotteryBtn.setVisibility(View.VISIBLE);
        });

        startCustomLotteryBtn.setOnClickListener(v -> {
            String sizeStr = participantNumberEditText.getText().toString().trim();

            if (TextUtils.isEmpty(sizeStr)) {
                participantNumberEditText.setError("Number of participants is required");
                return;
            }

            try {
                int size = Integer.parseInt(sizeStr);
                Intent intent = new Intent(OrganizerLotteryDrawActivity.this, OrganizerDrawStatusActivity.class);
                intent.putExtra("Event_ID", eventId);
                intent.putExtra("CUSTOM_SIZE", size);
                startActivity(intent);
            } catch (NumberFormatException e) {
                participantNumberEditText.setError("Please enter a valid number");
            }
        });
    }
}
