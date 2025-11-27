package com.example.dev.organizer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dev.firebaseobjects.EntrantAdapter;
import com.example.dev.firebaseobjects.FirebaseEntrant;
import com.example.dev.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrganizerDrawStatusActivity extends AppCompatActivity {
    private FirebaseFirestore database;
    private RecyclerView recyclerView;
    private EntrantAdapter entrantAdapter;
    private List<FirebaseEntrant> entrantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_draw_status);

        database = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.entrantRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        entrantList = new ArrayList<FirebaseEntrant>();
        entrantAdapter = new EntrantAdapter(entrantList);
        recyclerView.setAdapter(entrantAdapter);

        fetchEntrants();

        Button btnReplaceEntrant = findViewById(R.id.btn_Default_Size_Start);
        Button btnDeleteEntrant = findViewById(R.id.btn_Custom_Size_Start);

        btnReplaceEntrant.setOnClickListener(view -> {
            Toast.makeText(this, "Replace Entrant clicked", Toast.LENGTH_SHORT).show();
        });

        btnDeleteEntrant.setOnClickListener(view -> {
            Toast.makeText(this, "Delete Entrant clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchEntrants() {
        database.collection("entrants")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<FirebaseEntrant> entrants = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        FirebaseEntrant entrant = document.toObject(FirebaseEntrant.class);
                        if (entrant != null) {
                            entrants.add(entrant);
                        }
                    }
                    entrantList.clear();
                    entrantList.addAll(entrants);
                    entrantAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching entrants", Toast.LENGTH_SHORT).show();
                });
    }
}
