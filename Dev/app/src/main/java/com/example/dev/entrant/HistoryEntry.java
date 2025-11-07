package com.example.dev.entrant;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import java.time.Instant;

import com.example.dev.R;

public final class HistoryEntry {

    public enum State {
        UPCOMING(R.string.history_state_upcoming, R.color.history_state_upcoming, R.string.history_secondary_upcoming),
        COMPLETED(R.string.history_state_completed, R.color.history_state_completed, R.string.history_secondary_completed),
        SELECTED(R.string.history_state_selected, R.color.history_state_selected, R.string.history_secondary_selected),
        NOT_SELECTED(R.string.history_state_not_selected, R.color.history_state_not_selected, R.string.history_secondary_not_selected);

        @StringRes
        private final int labelRes;
        @ColorRes
        private final int chipColorRes;
        @StringRes
        private final int secondaryRes;

        State(@StringRes int labelRes, @ColorRes int chipColorRes, @StringRes int secondaryRes) {
            this.labelRes = labelRes;
            this.chipColorRes = chipColorRes;
            this.secondaryRes = secondaryRes;
        }

        public int getLabelRes() {
            return labelRes;
        }

        public int getChipColorRes() {
            return chipColorRes;
        }

        public int getSecondaryRes() {
            return secondaryRes;
        }
    }

    private final String eventName;
    private final State state;
    private final Instant joinedAt;
    @Nullable
    private final Instant statusChangedAt;

    public HistoryEntry(@NonNull String eventName,
                        @NonNull State state,
                        @NonNull Instant joinedAt,
                        @Nullable Instant statusChangedAt) {
        this.eventName = eventName;
        this.state = state;
        this.joinedAt = joinedAt;
        this.statusChangedAt = statusChangedAt;
    }

    public String getEventName() {
        return eventName;
    }

    public State getState() {
        return state;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    @Nullable
    public Instant getStatusChangedAt() {
        return statusChangedAt;
    }
}