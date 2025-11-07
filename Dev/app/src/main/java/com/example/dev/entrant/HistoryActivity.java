package com.example.dev.entrant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dev.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private HistoryAdapter adapter;
    private View emptyState;
    private RecyclerView recyclerView;
    private LinearProgressIndicator progressIndicator;

    public static Intent intent(@NonNull Context context) {
        return new Intent(context, HistoryActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entrant_history);


        MaterialToolbar toolbar = findViewById(R.id.toolbar_history);
        toolbar.setNavigationOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.history_root), (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(view.getPaddingLeft(), insets.top, view.getPaddingRight(), insets.bottom);
            return windowInsets;
        });

        recyclerView = findViewById(R.id.rv_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        adapter = new HistoryAdapter();
        recyclerView.setAdapter(adapter);

        emptyState = findViewById(R.id.empty_state);
        progressIndicator = findViewById(R.id.history_progress);
        progressIndicator.hide();

        MaterialButton backButton = findViewById(R.id.btn_history_back);
        backButton.setOnClickListener(v -> {
            Intent navIntent = new Intent(this, EntrantNavActivity.class);
            navIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(navIntent);
            finish();
        });

        loadHistory();
    }

    private void loadHistory() {
        String profileId = EntrantProfileStore.getProfileId(this);
        if (profileId == null) {
            showMessage(R.string.history_no_profile);
            renderHistory(new ArrayList<>());
            return;
        }

        progressIndicator.show();
        FirebaseFirestore.getInstance()
                .collection(EntrantProfileStore.COLLECTION_PROFILES)
                .document(profileId)
                .collection(EntrantProfileStore.COLLECTION_HISTORY)
                .orderBy("joinedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        seedDefaultHistory(profileId);
                        return;
                    }
                    List<HistoryEntry> entries = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        HistoryEntry entry = toHistoryEntry(doc);
                        if (entry != null) {
                            entries.add(entry);
                        }
                    }
                    renderHistory(entries);
                    progressIndicator.hide();
                })
                .addOnFailureListener(e -> {
                    showMessage(R.string.history_load_error);
                    renderHistory(new ArrayList<>());
                    progressIndicator.hide();
                });
    }
    private void renderHistory(List<HistoryEntry> entries) {
        adapter.submitList(entries);
        boolean isEmpty = entries.isEmpty();
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }


    private void seedDefaultHistory(@NonNull String profileId) {
        List<HistoryEntry> seeds = buildSeedEntries();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference historyCollection = firestore
                .collection(EntrantProfileStore.COLLECTION_PROFILES)
                .document(profileId)
                .collection(EntrantProfileStore.COLLECTION_HISTORY);

        WriteBatch batch = firestore.batch();
        for (HistoryEntry seed : seeds) {
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("eventName", seed.getEventName());
            data.put("state", seed.getState().name());
            data.put("joinedAt", new Timestamp(Date.from(seed.getJoinedAt())));
            if (seed.getStatusChangedAt() != null) {
                data.put("statusChangedAt", new Timestamp(Date.from(seed.getStatusChangedAt())));
            }
            batch.set(historyCollection.document(), data);
        }

        batch.commit()
                .addOnSuccessListener(unused -> renderHistory(seeds))
                .addOnFailureListener(e -> {
                    showMessage(R.string.history_load_error);
                    renderHistory(new ArrayList<>());
                })
                .addOnCompleteListener(task -> progressIndicator.hide());
    }

    private List<HistoryEntry> buildSeedEntries() {
        Instant now = Instant.now();
        List<HistoryEntry> seeds = new ArrayList<>();
        seeds.add(new HistoryEntry(
                getString(R.string.history_event_photowalk),
                HistoryEntry.State.UPCOMING,
                now.minus(5, ChronoUnit.DAYS),
                now.plus(10, ChronoUnit.DAYS)
        ));
        seeds.add(new HistoryEntry(
                getString(R.string.history_event_food_fest),
                HistoryEntry.State.SELECTED,
                now.minus(35, ChronoUnit.DAYS),
                now.minus(20, ChronoUnit.DAYS)
        ));
        seeds.add(new HistoryEntry(
                getString(R.string.history_event_charity_run),
                HistoryEntry.State.NOT_SELECTED,
                now.minus(70, ChronoUnit.DAYS),
                null
        ));
        seeds.add(new HistoryEntry(
                getString(R.string.history_event_hackathon),
                HistoryEntry.State.COMPLETED,
                now.minus(120, ChronoUnit.DAYS),
                now.minus(90, ChronoUnit.DAYS)
        ));
        return seeds;
    }

    private HistoryEntry toHistoryEntry(@NonNull DocumentSnapshot snapshot) {
        String name = snapshot.getString("eventName");
        String stateValue = snapshot.getString("state");
        Timestamp joinedAtValue = snapshot.getTimestamp("joinedAt");
        if (name == null || joinedAtValue == null) {
            return null;
        }
        Instant joinedAt = joinedAtValue.toDate().toInstant();
        Timestamp statusChangedAtValue = snapshot.getTimestamp("statusChangedAt");
        Instant statusChangedAt = statusChangedAtValue != null ? statusChangedAtValue.toDate().toInstant() : null;
        HistoryEntry.State state = HistoryEntry.State.fromStorageValue(stateValue);
        return new HistoryEntry(name, state, joinedAt, statusChangedAt);
    }

    private void showMessage(@StringRes int resId) {
        Snackbar.make(recyclerView, resId, Snackbar.LENGTH_LONG).show();
    }
}