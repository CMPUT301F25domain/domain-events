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
    private LinearLayout imagesContainer;
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

        loadAwsImages();
    }

    private void loadAwsImages() {
        db.collection("events")
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
                            Toast.makeText(getContext(), "No images found", Toast.LENGTH_SHORT).show();
                            imagesContainer.removeAllViews();
                            return;
                        }
                        // Clear existing views before reloading
                        imagesContainer.removeAllViews();

                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            String imageUrl = doc.getString("posterUrl");
                            if (imageUrl == null || imageUrl.isEmpty()) {
                                imageUrl = doc.getString("posterUri");
                            }

                            String imageTitle = doc.getString("title");
                            String location = doc.getString("location");

                            View imageView = getLayoutInflater().inflate(R.layout.item_admin_images, imagesContainer, false);

                            ImageView uploadedImage = imageView.findViewById(R.id.imageThumbnail);
                            TextView titleText = imageView.findViewById(R.id.imageTitleText);
                            TextView descText = imageView.findViewById(R.id.imageDescriptionText);
                            TextView removeButton = imageView.findViewById(R.id.removeButton);

                            if (imageTitle != null) {
                                titleText.setText(imageTitle);
                            } else {
                                titleText.setText("Untitled Image");
                            }

                            if (location != null) {
                                descText.setText(location);
                            } else {
                                descText.setText("Unknown Location");
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
                                        .placeholder(R.drawable.images)
                                        .error(R.drawable.images)
                                        .into(uploadedImage);
                            }

                            removeButton.setOnClickListener(v -> removePosterFromEvent(doc, uploadedImage));

                            imagesContainer.addView(imageView);
                        }
                    }
                });
    }
    private void removePosterFromEvent(DocumentSnapshot doc, ImageView uploadedImage) {
        doc.getReference().update("posterUrl", "", "posterUri", "")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Poster removed", Toast.LENGTH_SHORT).show();
                    uploadedImage.setImageResource(R.drawable.images);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to remove: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


}
