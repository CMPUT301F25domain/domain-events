package com.example.dev.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dev.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * AdminProfileFragment
 *
 * This fragment shows the Admin Profile screen in the Event Lottery app.
 * It’s part of the Admin section and uses the Fragment pattern to keep the UI
 * modular and easy to manage across different screens.
 *
 * Purpose:
 * - Displays all entrant and organizer profiles stored in Firestore.
 * - Shows each profile’s name, email, and phone number.
 * - Allows administrators to remove a profile directly from Firebase.
 *
 * Design Pattern:
 * - Uses the Fragment pattern for reusable, switchable UI sections inside the main admin activity.
 * - Updates the profile list using Firestore listeners.
 */
public class AdminProfileFragment extends Fragment {
    private LinearLayout profilesContainer;
    private FirebaseFirestore db;

    public AdminProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profilesContainer = view.findViewById(R.id.profilesContainer);
        db = FirebaseFirestore.getInstance();

        loadFirestoreProfiles();
    }

    private void loadFirestoreProfiles() {
        db.collection("entrants")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                        @Nullable FirebaseFirestoreException error) {
                        if (!isAdded() || getContext() == null || getView() == null) return;

                        if (error != null) {
                            Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (querySnapshot == null || querySnapshot.isEmpty()) {
                            Toast.makeText(getContext(), "No profiles found", Toast.LENGTH_SHORT).show();
                            profilesContainer.removeAllViews();
                            return;
                        }

                        // Clear existing views before reloading
                        profilesContainer.removeAllViews();

                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            String profileName = doc.getString("name");
                            String profileEmail = doc.getString("email");
                            String profilePhone = doc.getString("phone");

                            View profileView = getLayoutInflater().inflate(R.layout.item_admin_profile, profilesContainer, false);

                            TextView nameText = profileView.findViewById(R.id.profileName);
                            TextView emailText = profileView.findViewById(R.id.profileEmail);
                            TextView phoneText = profileView.findViewById(R.id.profilePhone);
                            TextView removeButton = profileView.findViewById(R.id.removeButton);

                            // If profile name is missing -> go to generic title
                            if (profileName != null) {
                                nameText.setText(profileName);
                            } else {
                                nameText.setText("Unknown User");
                            }
                            // If profile email is missing -> go to generic title
                            if (profileEmail != null) {
                                emailText.setText(profileEmail);
                            } else {
                                emailText.setText("No Email Provided");
                            }
                            // If profile phone is missing -> go to generic title
                            if (profilePhone != null) {
                                phoneText.setText(profilePhone);
                            } else {
                                phoneText.setText("No Phone Provided");
                            }


                            // Remove user from Firestore
                            removeButton.setOnClickListener(v -> {
                                doc.getReference().delete();
                            });

                            profilesContainer.addView(profileView);
                        }
                    }
                });

    }

}
