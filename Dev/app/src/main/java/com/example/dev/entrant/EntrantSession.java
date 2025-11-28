package com.example.dev.entrant;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.UUID;

/**
 * Provides a stable identifier for the current entrant so that Firestore documents
 * can be stored and retrieved consistently without an authentication layer.
 */
public final class EntrantSession {

    private static final String PREFS = "entrant_session";
    private static final String KEY_ID = "entrant_id";

    private EntrantSession() {
    }

    @NonNull
    public static String getCurrentEntrantId(@NonNull Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String id = prefs.getString(KEY_ID, null);
        if (id == null || id.trim().isEmpty()) {
            id = UUID.randomUUID().toString();
            prefs.edit().putString(KEY_ID, id).apply();
        }
        return id;
    }
}