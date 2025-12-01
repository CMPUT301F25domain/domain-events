/**
 * EntrantProfileFragment
 *
 * Allows entrant to view & edit profile.
 * Shows event participation history.
 */

package com.example.dev.entrant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import com.example.dev.R;
import com.example.dev.utils.DeviceIdUtil;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class EntrantProfileFragment extends Fragment {

    EditText inputName, inputEmail, inputPhone;
    Button btnSave;
    LinearLayout historyContainer;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String deviceId;

    public EntrantProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entrant_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputName = view.findViewById(R.id.inputName);
        inputEmail = view.findViewById(R.id.inputEmail);
        inputPhone = view.findViewById(R.id.inputPhone);
        btnSave = view.findViewById(R.id.btnSave);
        historyContainer = view.findViewById(R.id.historyContainer);

        deviceId = DeviceIdUtil.getDeviceId(requireContext());

        loadProfile();
        loadEventHistory();

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadProfile() {
        db.collection("entrants").document(deviceId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        inputName.setText(doc.getString("name"));
                        inputEmail.setText(doc.getString("email"));
                        inputPhone.setText(doc.getString("phone"));
                    }
                });
    }

    private void loadEventHistory() {
        db.collection("entrants").document(deviceId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    List<String> events = (List<String>) doc.get("joinedEvents");

                    if (events == null || events.isEmpty()) {
                        addHistoryText("No events yet.");
                        return;
                    }

                    for (String eventId : events) {
                        loadSingleEvent(eventId);
                    }
                });
    }

    private void loadSingleEvent(String eventId) {
        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(eventDoc -> {
                    if (!eventDoc.exists()) return;

                    String name = eventDoc.getString("eventName");
                    String location = eventDoc.getString("location");
                    String date = eventDoc.getString("eventDate");
                    String time = eventDoc.getString("eventTime");

                    String text =
                            "• " + name + "\n" +
                                    "  Location: " + location + "\n" +
                                    "  Date: " + date + "\n" +
                                    "  Time: " + time;

                    addHistoryText(text);
                });
    }

    private void addHistoryText(String text) {
        if (!isAdded()) return;

        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setTextSize(16);
        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        tv.setPadding(0, 10, 0, 10);

        historyContainer.addView(tv);
    }

    private void saveProfile() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", inputName.getText().toString());
        data.put("email", inputEmail.getText().toString());
        data.put("phone", inputPhone.getText().toString());

        db.collection("entrants").document(deviceId)
                .update(data)
                .addOnSuccessListener(a ->
                        Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to update.", Toast.LENGTH_SHORT).show());
    }
}
