package com.example.dev.organizer.repo.impl;

import com.example.dev.organizer.Entrant;
import java.util.List;

public interface WaitingListRepository {
    interface Callback {
        void onSuccess(List<Entrant> entrants);
        void onError(Exception e);
    }
    void list(String eventId, Callback callback);
    List<Entrant> cached(String eventId);
    int cachedCount(String eventId);
    void add(String eventId, Entrant e);
    void remove(String eventId, Entrant e);
}