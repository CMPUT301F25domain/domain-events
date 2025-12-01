package com.example.dev.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.dev.utils.NotificationHelper;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MessageListenerService extends Service {
    private FirebaseFirestore database;
    private ListenerRegistration listenerRegistration;
    private String androidId;
    private Set<String> processedMessageIds = new HashSet<>();

    @Override
    public void onCreate() {
        super.onCreate();
        database = FirebaseFirestore.getInstance();
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        startListening();
    }

    private void startListening() {
        DocumentReference entrantRef = database.collection("entrants").document(androidId);

        listenerRegistration = entrantRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("MessageListener", "Listen failed", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                List<Map<String, Object>> messages = (List<Map<String, Object>>) snapshot.get("Message");

                if (messages != null) {
                    for (Map<String, Object> message : messages) {
                        String eventId = (String) message.get("eventId");
                        Boolean lotteryStatus = (Boolean) message.get("lotteryStatus");
                        String lotteryMessage = (String) message.get("lotteryMessage");
                        String eventName = (String) message.get("eventName");

                        if (eventId != null && lotteryStatus != null) {
                            String messageId = eventId + "_" + lotteryStatus;

                            if (!processedMessageIds.contains(messageId)) {
                                processedMessageIds.add(messageId);

                                String title = lotteryStatus ? "Congratulations!" : "Lottery Result";
                                String messageText = lotteryMessage != null ? lotteryMessage :
                                        (lotteryStatus ? "You've been selected!" : "Not selected this time");

                                NotificationHelper.sendLotteryNotification(
                                        getApplicationContext(),
                                        title,
                                        messageText,
                                        lotteryStatus,
                                        androidId
                                );
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}