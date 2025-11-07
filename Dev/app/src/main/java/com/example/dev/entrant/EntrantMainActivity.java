package com.example.dev.entrant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dev.MainActivity;
import com.example.dev.R;
import com.example.dev.entrant.adapters.EntrantEventAdapter;
import com.example.dev.entrant.models.EntrantEvent;
import com.example.dev.organizer.EventAdapter;
import com.example.dev.entrant.EntrantMessagesActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class EntrantMainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EntrantEventAdapter adapter;
    ArrayList<EntrantEvent> eventList = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_activity_main);

        MaterialToolbar toolbar = findViewById(R.id.event_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        getSupportActionBar().setTitle("Events");

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_messages) {
                Intent intent = new Intent(EntrantMainActivity.this, EntrantMessagesActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(EntrantMainActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // prevents stacking screens
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.eventRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EntrantEventAdapter(this, eventList);
        recyclerView.setAdapter(adapter);

        loadEvents();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_message_menu, menu);
        return true;
    }

    private void loadEvents() {
        db.collection("events").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("FIRESTORE_ERROR", error.getMessage());
                return;
            }

            eventList.clear();
            for (DocumentSnapshot doc : value.getDocuments()) {
                EntrantEvent event = doc.toObject(EntrantEvent.class);
                eventList.add(event);
            }

            adapter.notifyDataSetChanged();
        });
    }
}
