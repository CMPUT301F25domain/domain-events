package com.example.dev.entrant;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

final class EntrantProfileStore {

    static final String COLLECTION_PROFILES = "profiles";
    static final String COLLECTION_HISTORY = "history";

    private static final String PREFS_PROFILE = "entrant_profile";
    private static final String KEY_PROFILE_ID = "profile_id";

    private EntrantProfileStore() {
    }

    @Nullable
    static String getProfileId(@NonNull Context context) {
        return getPrefs(context).getString(KEY_PROFILE_ID, null);
    }

    @NonNull
    static String ensureProfileId(@NonNull Context context) {
        SharedPreferences prefs = getPrefs(context);
        String id = prefs.getString(KEY_PROFILE_ID, null);
        if (id == null) {
            id = UUID.randomUUID().toString();
            prefs.edit().putString(KEY_PROFILE_ID, id).apply();
        }
        return id;
    }

    static void clearProfileId(@NonNull Context context) {
        getPrefs(context).edit().remove(KEY_PROFILE_ID).apply();
    }

    @NonNull
    static CollectionReference profilesCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_PROFILES);
    }

    @NonNull
    private static SharedPreferences getPrefs(@NonNull Context context) {
        return context.getSharedPreferences(PREFS_PROFILE, Context.MODE_PRIVATE);
    }
}