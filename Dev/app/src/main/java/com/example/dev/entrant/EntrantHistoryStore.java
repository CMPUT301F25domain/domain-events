package com.example.dev.entrant;

import android.content.Context;

import androidx.annotation.NonNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.dev.R;

final class EntrantHistoryStore {

    private EntrantHistoryStore() {
    }

    static List<HistoryEntry> getHistory(@NonNull Context context) {
        List<HistoryEntry> items = new ArrayList<>();
        Instant now = Instant.now();
        items.add(new HistoryEntry(
                context.getString(R.string.history_event_photowalk),
                HistoryEntry.State.UPCOMING,
                now.minus(5, ChronoUnit.DAYS),
                now.plus(10, ChronoUnit.DAYS)
        ));
        items.add(new HistoryEntry(
                context.getString(R.string.history_event_food_fest),
                HistoryEntry.State.SELECTED,
                now.minus(35, ChronoUnit.DAYS),
                now.minus(20, ChronoUnit.DAYS)
        ));
        items.add(new HistoryEntry(
                context.getString(R.string.history_event_charity_run),
                HistoryEntry.State.NOT_SELECTED,
                now.minus(70, ChronoUnit.DAYS),
                null
        ));
        items.add(new HistoryEntry(
                context.getString(R.string.history_event_hackathon),
                HistoryEntry.State.COMPLETED,
                now.minus(120, ChronoUnit.DAYS),
                now.minus(90, ChronoUnit.DAYS)
        ));
        return Collections.unmodifiableList(items);
    }
}
