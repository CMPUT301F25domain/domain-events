/**
 * EntrantHomeFragment
 *
 * Home screen for entrant users.
 * Displays event list and provides navigation to QR scanner.
 */

package com.example.dev.entrant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dev.R;
import com.example.dev.entrant.QRCodeScannerActivity;
import com.example.dev.entrant.adapters.EntrantEventAdapter;
import com.example.dev.entrant.models.EntrantEvent;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EntrantHomeFragment extends Fragment {

    RecyclerView recyclerView;
    EntrantEventAdapter adapter;
    ArrayList<EntrantEvent> eventList = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public EntrantHomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.entrant_activity_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView qrIcon = view.findViewById(R.id.qrScanIcon);


        qrIcon.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), QRCodeScannerActivity.class);
            startActivity(i);
        });

        recyclerView = view.findViewById(R.id.eventRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new EntrantEventAdapter(getContext(), eventList);
        recyclerView.setAdapter(adapter);

        loadEvents();
    }

    private void loadEvents() {
        db.collection("events").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("FIRESTORE_ERROR", error.getMessage());
                return;
            }

            eventList.clear();

            for (DocumentSnapshot doc : value.getDocuments()) {
                EntrantEvent event = doc.toObject(EntrantEvent.class);
                if (event != null) {
                    event.setEventId(doc.getId());
                    eventList.add(event);
                }
            }

            adapter.notifyDataSetChanged();
        });
    }
}
