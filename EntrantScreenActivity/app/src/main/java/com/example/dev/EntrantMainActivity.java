package com.example.dev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.example.dev.adapters.EventAdapter;
import com.example.dev.models.Event;
import com.example.dev.views.MessagesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EventAdapter adapter;
    ArrayList<Event> eventList = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.event_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Events");

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_messages) {
                Intent intent = new Intent(MainActivity.this, MessagesActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        FirebaseAuth.getInstance().signInAnonymously();
        recyclerView = findViewById(R.id.eventRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EventAdapter(this, eventList);
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
                Event event = doc.toObject(Event.class);
                eventList.add(event);
            }

            adapter.notifyDataSetChanged();
        });
    }
}
