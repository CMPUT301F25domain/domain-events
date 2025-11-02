package com.example.dev;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.btnOrganizer).setOnClickListener(v ->
                Toast.makeText(this, "Organizer dashboard here", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnEntrant).setOnClickListener(v ->
                Toast.makeText(this, "Entrant dashboard here", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnAdmin).setOnClickListener(v ->
                startActivity(new Intent(this, com.example.dev.admin.AdminNavActivity.class)));
    }
}