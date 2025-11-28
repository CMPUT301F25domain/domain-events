/**
 * EntrantMessagesFragment
 *
 * Displays messages sent to the entrant (lottery results, updates, etc).
 * Reads "Message" array from entrants/{deviceId}.
 */

package com.example.dev.entrant;

import android.content.Intent;
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
import com.example.dev.utils.DeviceIdUtil;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class EntrantMessagesFragment extends Fragment {

    private LinearLayout messageContainer;
    private TextView emptyMessageText;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String deviceId;

    public EntrantMessagesFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entrant_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        messageContainer = view.findViewById(R.id.messageContainer);
        emptyMessageText = view.findViewById(R.id.emptyMessageText);

        deviceId = DeviceIdUtil.getDeviceId(requireContext());

        loadMessages();
    }

    private void loadMessages() {
        db.collection("entrants").document(deviceId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!isAdded()) return;
                    if (!doc.exists()) return;

                    List<Map<String, Object>> messages =
                            (List<Map<String, Object>>) doc.get("Message");

                    if (messages == null || messages.isEmpty()) {
                        emptyMessageText.setVisibility(View.VISIBLE);
                        return;
                    }

                    emptyMessageText.setVisibility(View.GONE);

                    for (Map<String, Object> msg : messages) {
                        addMessageCard(msg);
                    }
                });
    }

    private void addMessageCard(Map<String, Object> msg) {
        if (!isAdded()) return;

        String eventId = (String) msg.get("eventId");
        String eventName = (String) msg.get("eventName");
        String eventDate = (String) msg.get("eventDate");
        String eventLocation = (String) msg.get("eventLocation");
        String lotteryMessage = (String) msg.get("lotteryMessage");
        boolean lotteryStatus = Boolean.TRUE.equals(msg.get("lotteryStatus"));

        TextView tv = new TextView(requireContext());

        String text = lotteryMessage +
                "\nEvent: " + eventName +
                "\nDate: " + eventDate +
                "\nLocation: " + eventLocation;

        tv.setText(text);
        tv.setTextSize(16);
        tv.setPadding(20, 20, 20, 20);
        tv.setBackgroundColor(lotteryStatus ? 0xFFDFFFD6 : 0xFFFFE5E5);
        tv.setTextColor(0xFF000000);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        params.setMargins(0, 10, 0, 10);
        tv.setLayoutParams(params);

        if (lotteryStatus && eventId != null) {
            tv.setOnClickListener(v -> {
                Intent i = new Intent(getActivity(), InvitationDetailsActivity.class);
                i.putExtra("eventId", eventId);
                startActivity(i);
            });
        }

        messageContainer.addView(tv);
    }
}
