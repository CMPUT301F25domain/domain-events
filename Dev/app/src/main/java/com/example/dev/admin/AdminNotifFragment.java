package com.example.dev.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dev.R;

/**
 * AdminNotifFragment
 *
 * This fragment shows the Admin Notifications screen in the Event Lottery app.
 * It’s part of the Admin section and uses the Fragment pattern to keep the UI
 * modular and easy to manage across different screens.
 *
 * Purpose:
 * - Displays an empty placeholder screen for admin notifications.
 * - Serves as the destination screen when the bell icon is clicked from the Admin Events page.
 *
 * Design Pattern:
 * - Uses the Fragment pattern for reusable, switchable UI sections inside the main admin activity.
 *
 * Outstanding Issues:
 * - Implement a Firebase connection to display the real notifications.
 */

public class AdminNotifFragment extends Fragment {

    public AdminNotifFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_notifications, container, false);
    }
}
