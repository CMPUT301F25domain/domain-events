package com.example.dev.views;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dev.R;
import com.google.android.material.appbar.MaterialToolbar;

public class MessagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

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
