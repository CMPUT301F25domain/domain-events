package com.example.dev.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dev.MainActivity;
import com.example.dev.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * AdminEventsFragment
 *
 * This fragment shows the Admin Events screen in the Event Lottery app.
 * It’s part of the Admin section and uses the Fragment pattern to keep the UI
 * modular and easy to manage across different screens.
 *
 * Purpose:
 * - Shows a list of sample events that admins can view or remove.
 * - Each event has a “Remove” button that hides it from the list.
 *
 * Design Pattern:
 * - Uses the Fragment pattern for reusable, switchable UI sections inside the main activity.
 *
 * Outstanding Issues:
 * - Display image beside each event that is not a sample swimming image, but uploaded to firebase by organizer.
 */

public class AdminEventsFragment extends Fragment {

    private LinearLayout eventsContainer;
    private FirebaseFirestore db;

    public AdminEventsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventsContainer = view.findViewById(R.id.eventsContainer);
        db = FirebaseFirestore.getInstance();

        ImageView bellIcon = view.findViewById(R.id.bellIcon);
        bellIcon.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AdminNotifFragment())
                    .addToBackStack(null)
                    .commit();
        });

        ImageView backIcon = view.findViewById(R.id.backIcon);
        backIcon.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });


        loadFirestoreEvents();
    }
    /*
        Source: GeeksforGeeks
        Title: "How to Fetch Data from Firebase Firestore in Batches with Limit in Android?"
        Last Updated: July 23, 2025
        License: CC BY-SA 4.0 (International)
        URL: https://www.geeksforgeeks.org/android/how-to-fetch-data-from-firebase-firestore-in-batches-with-limit-in-android/
    */
    private void loadFirestoreEvents() {
        db.collection("events")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                        @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (querySnapshot == null || querySnapshot.isEmpty()) {
                            Toast.makeText(getContext(), "No events found", Toast.LENGTH_SHORT).show();
                            eventsContainer.removeAllViews();
                            return;
                        }

                        // Clear existing views before reloading
                        eventsContainer.removeAllViews();


                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            String eventName = doc.getString("title");
                            String eventStart = doc.getString("eventStart");
                            String eventEnd = doc.getString("eventEnd");
                            String eventTime = doc.getString("eventTime");
                            String location = doc.getString("location");

                            View eventView = getLayoutInflater().inflate(R.layout.item_admin_event, eventsContainer, false);

                            /*
                                Source: Stack Overflow
                                Title: "How to display message from firebase to TextView Android Studio?"
                                Author: user "Ticherhaz FreePalestine"
                                Date: October 25, 2019
                                License: CC BY-SA 4.0 (International)
                                URL: https://stackoverflow.com/a/58551914

                            */
                            TextView nameText = eventView.findViewById(R.id.eventName);
                            TextView locationText = eventView.findViewById(R.id.eventLocation);
                            TextView closeText = eventView.findViewById(R.id.eventClose);
                            TextView removeButton = eventView.findViewById(R.id.removeButton);

                            /*
                                Source: Stack Overflow
                                Title: "Android - Set text to TextView"
                                Author: user "Ankush Joshi"
                                Date: March 8, 2016
                                License: CC BY-SA 4.0 (International)
                                URL: https://stackoverflow.com/a/35861055
                            */
                            if (eventName != null) {
                                nameText.setText(eventName);
                            } else {
                                nameText.setText("Unnamed Event");
                            }

                            if (location != null) {
                                locationText.setText(location);
                            } else {
                                locationText.setText("Unknown location");
                            }


                            // “Registration closes” line
                            if (eventEnd != null && eventTime != null) {
                                closeText.setText("Registration closes on " + eventEnd + " at " + eventTime);
                            } else if (eventEnd != null) {
                                closeText.setText("Registration closes on " + eventEnd);
                            } else {
                                closeText.setText("Registration closing date unavailable");
                            }

                            /*
                                Source: GoogleCloud
                                Title: "Delete a Firestore collection”
                                Author/Entity: GoogleCloud
                                License: CC BY 4.0
                                URL:  https://cloud.google.com/firestore/docs/samples/firestore-data-delete-collection#firestore_data_delete_collection-java
                            */
                            removeButton.setOnClickListener(v -> {
                                doc.getReference().delete();
                            });

                            eventsContainer.addView(eventView);
                        }
                    }
                });
    }
}
