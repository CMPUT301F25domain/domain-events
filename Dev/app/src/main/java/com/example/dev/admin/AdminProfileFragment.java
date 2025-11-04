package com.example.dev.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dev.R;

/**
 * AdminProfileFragment
 *
 * This fragment shows the Admin Profile screen in the Event Lottery app.
 * It lists user profiles that admins can remove.
 *
 * Purpose:
 * - Displays a list of users with their name, email, and role.
 * - Each user card has a “Remove” option that hides that profile.
 *
 * Design Pattern:
 * - Uses the Fragment pattern for modular UI and navigation via BottomNavigationView.
 *
 * Outstanding Issues:
 * - Make remove actually delete the event from Firebase instead of just hiding it.
 */
public class AdminProfileFragment extends Fragment {

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

        // Array of layout and remove TextView pairs
        int[][] userPairs = {
                {R.id.user1, R.id.removeUser1},
                {R.id.user2, R.id.removeUser2},
                {R.id.user3, R.id.removeUser3}
        };

        // Loop through all user-remove pairs and attach listeners
        for (int[] pair : userPairs) {
            LinearLayout userLayout = view.findViewById(pair[0]);
            TextView removeButton = view.findViewById(pair[1]);
            removeButton.setOnClickListener(v -> userLayout.setVisibility(View.GONE));
        }
    }
}
