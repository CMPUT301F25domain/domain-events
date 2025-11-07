package com.example.dev;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.admin.AdminNavActivity;
import com.example.dev.organizer.OrganizerDashboardActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Organizer button
        findViewById(R.id.btnOrganizer).setOnClickListener(v -> {
            Intent intent = new Intent(this, OrganizerDashboardActivity.class);
            startActivity(intent);
        });

        // Entrant button
// Entrant button → go to entrant event list
        findViewById(R.id.btnEntrant).setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.dev.entrant.EntrantMainActivity.class);
            startActivity(intent);
        });

        // Admin button
        findViewById(R.id.btnAdmin).setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminNavActivity.class);
            startActivity(intent);
        });
    }
}
