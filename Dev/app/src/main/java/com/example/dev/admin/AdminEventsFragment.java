package com.example.dev.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dev.R;

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
 * - Make the bell icon open the notification logs.
 * - Make remove actually delete the event from Firebase instead of just hiding it.
 * - Finish and polish the UI design later.
 */


public class AdminEventsFragment extends Fragment {

    public AdminEventsFragment() {
    }

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

        // Array of event layouts and remove button pairs
        int[][] eventPairs = {
                {R.id.event1, R.id.removeEvent1},
                {R.id.event2, R.id.removeEvent2},
                {R.id.event3, R.id.removeEvent3},
                {R.id.event4, R.id.removeEvent4},
                {R.id.event5, R.id.removeEvent5}
        };

        // Loop through all event pairs to attach remove listeners
        for (int[] pair : eventPairs) {
            LinearLayout eventLayout = view.findViewById(pair[0]);
            TextView removeButton = view.findViewById(pair[1]);
            removeButton.setOnClickListener(v -> eventLayout.setVisibility(View.GONE));
        }

        ImageView bellIcon = view.findViewById(R.id.bellIcon);
        bellIcon.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AdminNotifFragment())
                    .addToBackStack(null) // allows back button to return to Events
                    .commit();
        });



    }

}
