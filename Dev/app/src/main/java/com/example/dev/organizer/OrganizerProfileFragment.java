package com.example.dev.organizer;

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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dev.R;
import com.example.dev.utils.DeviceIdUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class OrganizerProfileFragment extends Fragment {

    private static final String ARG_ORGANIZER_ID = "organizerID";

    private EditText inputName;
    private EditText inputEmail;
    private EditText inputPhone;
    private Button btnSave;
    private LinearLayout historyContainer;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String organizerId;

    public static OrganizerProfileFragment newInstance(String organizerId) {
        OrganizerProfileFragment fragment = new OrganizerProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORGANIZER_ID, organizerId);
        fragment.setArguments(args);
        return fragment;
    }

    public OrganizerProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organizer_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final int paddingLeft = view.getPaddingLeft();
        final int paddingTop = view.getPaddingTop();
        final int paddingRight = view.getPaddingRight();
        final int paddingBottom = view.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(paddingLeft, paddingTop + statusBars.top, paddingRight, paddingBottom);
            return insets;
        });
        ViewCompat.requestApplyInsets(view);


        inputName = view.findViewById(R.id.inputName);
        inputEmail = view.findViewById(R.id.inputEmail);
        inputPhone = view.findViewById(R.id.inputPhone);
        btnSave = view.findViewById(R.id.btnSave);
        historyContainer = view.findViewById(R.id.historyContainer);

        organizerId = getArguments() != null ? getArguments().getString(ARG_ORGANIZER_ID) : null;
        if (organizerId == null && getContext() != null) {
            organizerId = DeviceIdUtil.getDeviceId(getContext());
        }

        loadProfile();
        loadEventHistory();

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadProfile() {
        if (organizerId == null) return;

        db.collection("organizers").document(organizerId)
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
        if (organizerId == null) return;

        db.collection("events")
                .whereEqualTo("organizerId", organizerId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        addHistoryText("No events yet.");
                        return;
                    }

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        addHistoryText(formatEvent(doc));
                    }
                });
    }

    private String formatEvent(QueryDocumentSnapshot eventDoc) {
        String name = eventDoc.getString("eventName");
        String location = eventDoc.getString("location");
        String date = eventDoc.getString("eventDate");
        String time = eventDoc.getString("eventTime");

        return "• " + name + "\n" +
                "  Location: " + location + "\n" +
                "  Date: " + date + "\n" +
                "  Time: " + time;
    }

    private void addHistoryText(String text) {
        if (!isAdded()) return;

        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setTextSize(16);
        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_text_primary));
        tv.setPadding(0, 10, 0, 10);

        historyContainer.addView(tv);
    }

    private void saveProfile() {
        if (organizerId == null) return;

        Map<String, Object> data = new HashMap<>();
        data.put("name", inputName.getText().toString());
        data.put("email", inputEmail.getText().toString());
        data.put("phone", inputPhone.getText().toString());

        db.collection("organizers").document(organizerId)
                .update(data)
                .addOnSuccessListener(a ->
                        Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to update.", Toast.LENGTH_SHORT).show());
    }
}