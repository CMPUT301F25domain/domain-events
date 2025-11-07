package com.example.dev.entrant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private HistoryAdapter adapter;

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

        RecyclerView recyclerView = findViewById(R.id.rv_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        adapter = new HistoryAdapter();
        recyclerView.setAdapter(adapter);

        View emptyState = findViewById(R.id.empty_state);
        MaterialButton backButton = findViewById(R.id.btn_history_back);
        backButton.setOnClickListener(v -> {
            Intent navIntent = new Intent(this, EntrantNavActivity.class);
            navIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(navIntent);
            finish();
        });

        List<HistoryEntry> items = EntrantHistoryStore.getHistory(this);
        renderHistory(items, emptyState, recyclerView);
    }

    private void renderHistory(List<HistoryEntry> entries, View emptyState, RecyclerView recyclerView) {
        adapter.submitList(entries);
        boolean isEmpty = entries.isEmpty();
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}
