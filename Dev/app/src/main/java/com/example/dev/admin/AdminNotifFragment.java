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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * AdminNotifFragment
 *
 * This fragment shows the Admin Notifications screen in the Event Lottery app.
 * It’s part of the Admin section and uses the Fragment pattern to keep the UI
 * modular and easy to manage across different screens.
 *
 * Purpose:
 * - Displays all notifications sent to entrants.
 * - Shows each notification’s event details and message content.
 *
 * Design Pattern:
 * - Uses the Fragment pattern for reusable, switchable UI sections inside the main admin activity.
 * - Updates the notification list using Firestore listeners.
 */

public class AdminNotifFragment extends Fragment {
    private LinearLayout notifContainer;
    private FirebaseFirestore db;

    public AdminNotifFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notifContainer = view.findViewById(R.id.notifContainer);
        db = FirebaseFirestore.getInstance();

        loadFirestoreNotifications();
    }
    private void loadFirestoreNotifications() {
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
                            Toast.makeText(getContext(), "No notifications found", Toast.LENGTH_SHORT).show();
                            notifContainer.removeAllViews();
                            return;
                        }

                        // Clear existing views before reloading
                        notifContainer.removeAllViews();

                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            List<Map<String, Object>> messageList =
                                    (List<Map<String, Object>>) doc.get("Message");
                            // If there is not message array, skip
                            if (messageList == null || messageList.isEmpty()) continue;

                            // Go through each message in array and display
                            for (Map<String, Object> msg : messageList) {
                                if (msg == null) continue;
                                String eventName = (String) msg.get("eventName");
                                String eventDate = (String) msg.get("eventDate");
                                String eventLocation = (String) msg.get("eventLocation");
                                String lotteryMessage = (String) msg.get("lotteryMessage");
                                String entrantEmail = doc.getString("email");

                                View notifView = getLayoutInflater().inflate(R.layout.item_admin_notification, notifContainer, false);

                                TextView titleText = notifView.findViewById(R.id.notifTitle);
                                TextView bodyText = notifView.findViewById(R.id.notifBody);

                                // If event name is missing -> go to generic title
                                if (eventName != null) {
                                    titleText.setText(eventName);
                                } else {
                                    titleText.setText("Event Notification");
                                }

                                // Build message body for notification box
                                String body = "Entrant: " + entrantEmail + "\n"  +lotteryMessage + "\n" +
                                        "Date: " + eventDate + "\n" +
                                        "Location: " + eventLocation;

                                bodyText.setText(body);
                                notifContainer.addView(notifView);
                            }
                        }
                    }
                });

    }
}
