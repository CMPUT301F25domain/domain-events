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

        // Event layouts and remove buttons
        LinearLayout event1 = view.findViewById(R.id.event1);
        TextView remove1 = view.findViewById(R.id.removeEvent1);

        LinearLayout event2 = view.findViewById(R.id.event2);
        TextView remove2 = view.findViewById(R.id.removeEvent2);

        LinearLayout event3 = view.findViewById(R.id.event3);
        TextView remove3 = view.findViewById(R.id.removeEvent3);

        LinearLayout event4 = view.findViewById(R.id.event4);
        TextView remove4 = view.findViewById(R.id.removeEvent4);

        LinearLayout event5 = view.findViewById(R.id.event5);
        TextView remove5 = view.findViewById(R.id.removeEvent5);

        // Remove event functionality
        remove1.setOnClickListener(v -> event1.setVisibility(View.GONE));
        remove2.setOnClickListener(v -> event2.setVisibility(View.GONE));
        remove3.setOnClickListener(v -> event3.setVisibility(View.GONE));
        remove4.setOnClickListener(v -> event4.setVisibility(View.GONE));
        remove5.setOnClickListener(v -> event5.setVisibility(View.GONE));

        // Bell icons
        ImageView bellIcon = view.findViewById(R.id.bellIcon);

        // When bell is clicked
        //bellIcon.setOnClickListener(v -> {
            // Replace current fragment with AdminLogsFragment
           // requireActivity().getSupportFragmentManager()
                    //.beginTransaction()
                   // .replace(R.id.fragment_container, new AdminLogsFragment())
                    //.addToBackStack(null)
                   // .commit();
        //});

    }
}
