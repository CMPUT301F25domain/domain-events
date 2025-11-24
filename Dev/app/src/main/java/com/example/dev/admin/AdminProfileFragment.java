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
 * AdminProfileFragment
 *
 * This fragment shows the Admin Profile screen in the Event Lottery app.
 * It’s part of the Admin section and uses the Fragment pattern to keep the UI
 * modular and easy to manage across different screens.
 *
 * Purpose:
 * - Displays a list of sample user profiles that admins can view or remove.
 * - Each user layout has a “Remove” button that hides that user’s profile from the list.
 *
 * Design Pattern:
 * - Uses the Fragment pattern for reusable, switchable UI sections inside the main admin activity.
 *
 * Outstanding Issues:
 * - Replace mock data users with data loaded from Firebase.
 * - Make remove actually delete the profile from Firebase instead of just hiding it.
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

    private void loadAdminProfile() {
        db.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                        @Nullable FirebaseFirestoreException error) {
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

                        // Loop through all user documents
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            String name = doc.getString("name");
                            String email = doc.getString("email");
                            String role = doc.getString("role");

                            View profileView = getLayoutInflater().inflate(R.layout.item_admin_profile, profilesContainer, false);

                            TextView nameText = profileView.findViewById(R.id.userName);
                            TextView emailText = profileView.findViewById(R.id.userEmail);
                            TextView roleText = profileView.findViewById(R.id.userRole);
                            TextView removeButton = profileView.findViewById(R.id.removeButton);

                            if (name != null) {
                                nameText.setText(name);
                            } else {
                                nameText.setText("Unknown User");
                            }

                            if (email != null) {
                                emailText.setText(email);
                            } else {
                                emailText.setText("No Email Provided");
                            }

                            if (role != null) {
                                roleText.setText(role);
                            } else {
                                roleText.setText("No Role Assigned");
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
