package com.example.dev;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.admin.AdminNavActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Organizer button
        findViewById(R.id.btnOrganizer).setOnClickListener(v ->
                Toast.makeText(this, "Organizer dashboard here", Toast.LENGTH_SHORT).show());

        // Entrant button
        findViewById(R.id.btnEntrant).setOnClickListener(v ->
                Toast.makeText(this, "Entrant dashboard here", Toast.LENGTH_SHORT).show());

        // Admin button
        findViewById(R.id.btnAdmin).setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminNavActivity.class);
            startActivity(intent);
        });
    }
}
