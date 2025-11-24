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

import com.bumptech.glide.Glide;
import com.example.dev.MainActivity;
import com.example.dev.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * AdminImageFragment
 *
 * This fragment shows the Admin Images screen in the Event Lottery app.
 * It’s part of the Admin section and uses the Fragment pattern to keep the UI
 * modular and easy to manage across different screens.
 *
 * Purpose:
 * - Displays a list of uploaded images that admins can view or remove.
 * - Each event has a “Remove” button that hides it from the list.
 *
 * Design Pattern:
 * - Uses the Fragment pattern for reusable, switchable UI sections inside the main admin activity.
 *
 * Outstanding Issues:
 * - Not have the images be mock data but instead the result of organizers uploading images
 * - Make remove actually delete the image from Firebase instead of just hiding it.
 */


public class AdminImageFragment extends Fragment {
    private LinearLayout eventsContainer;
    private FirebaseFirestore db;

    public AdminImageFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_images, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imagesContainer = view.findViewById(R.id.imagesContainer);
        db = FirebaseFirestore.getInstance();

        loadFirestoreImages();
    }

        private void loadFirestoreImages() {
            db.collection("images")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                            @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                                return;
                            }

                            if (querySnapshot == null || querySnapshot.isEmpty()) {
                                Toast.makeText(getContext(), "No images found", Toast.LENGTH_SHORT).show();
                                imagesContainer.removeAllViews();
                                return;
                            }
                            // Clear existing views before reloading
                            imagesContainer.removeAllViews();

                            for (DocumentSnapshot doc : snapshots.getDocuments()) {
                                String imageUrl = doc.getString("imageUrl");
                                String imageTitle = doc.getString("title");

                                View imageView = getLayoutInflater().inflate(R.layout.item_admin_image, imagesContainer, false);

                                ImageView uploadedImage = imageView.findViewById(R.id.uploadedImage);
                                TextView titleText = imageView.findViewById(R.id.imageTitle);
                                TextView removeButton = imageView.findViewById(R.id.removeImageButton);

                                if (imageTitle != null) {
                                    titleText.setText(imageTitle);
                                } else {
                                    titleText.setText("Untitled Image");
                                }

                                /*
                                    Source: github.io
                                    Title: "Glide v4: Getting Started - GitHub pages"
                                    License: BSD 2-Clause License
                                    URL: https://bumptech.github.io/glide/doc/getting-started.html
                                 */
                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    Glide.with(requireContext())
                                            .load(imageUrl)
                                            .placeholder(R.drawable.placeholder)
                                            .into(uploadedImage);
                                }

                                removeButton.setOnClickListener(v -> {
                                    doc.getReference().delete();
                                });

                                imagesContainer.addView(imageView);
                            }
                        }
                    });
    }
}
