package com.example.dev.entrant;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dev.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.textview.MaterialTextView;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final List<HistoryEntry> items = new ArrayList<>();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter
            .ofPattern("MMM d, yyyy h:mm a", Locale.getDefault())
            .withZone(ZoneId.systemDefault());

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void submitList(@NonNull List<HistoryEntry> entries) {
        items.clear();
        items.addAll(entries);
        notifyDataSetChanged();
    }

    private String formatInstant(@NonNull Instant instant) {
        return dateTimeFormatter.format(instant);
    }

    private String buildSecondaryText(@NonNull Context context, @NonNull HistoryEntry entry) {
        Instant updatedAt = entry.getStatusChangedAt();
        String formattedTime = updatedAt != null ? formatInstant(updatedAt) : context.getString(R.string.history_secondary_no_date);
        switch (entry.getState()) {
            case UPCOMING:
                return context.getString(R.string.history_secondary_upcoming, formattedTime);
            case COMPLETED:
                return context.getString(R.string.history_secondary_completed, formattedTime);
            case SELECTED:
                return context.getString(R.string.history_secondary_selected, formattedTime);
            case NOT_SELECTED:
            default:
                return context.getString(R.string.history_secondary_not_selected);
        }
    }

    final class HistoryViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView titleView;
        private final MaterialTextView joinedAtView;
        private final MaterialTextView secondaryView;
        private final Chip stateChip;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.tv_event_title);
            joinedAtView = itemView.findViewById(R.id.tv_joined_at);
            secondaryView = itemView.findViewById(R.id.tv_secondary_time);
            stateChip = itemView.findViewById(R.id.chip_state);
        }

        void bind(@NonNull HistoryEntry entry) {
            Context context = itemView.getContext();
            titleView.setText(entry.getEventName());
            joinedAtView.setText(context.getString(R.string.history_joined_at, formatInstant(entry.getJoinedAt())));
            secondaryView.setText(buildSecondaryText(context, entry));
            stateChip.setText(context.getString(entry.getState().getLabelRes()));
            ColorStateList colorStateList = ColorStateList.valueOf(ContextCompat.getColor(context, entry.getState().getChipColorRes()));
            stateChip.setChipBackgroundColor(colorStateList);
            stateChip.setTextColor(ContextCompat.getColor(context, R.color.history_state_on_chip));
        }
    }
}