package com.example.dev.organizer.repo.impl;

import com.example.dev.organizer.Entrant;
import com.example.dev.repo.WaitingListRepository;

import java.util.ArrayList;
import java.util.List;

/** In-memory waiting list backed by shared InMemoryStore. */
public class WaitingListRepoMem implements WaitingListRepository {

    @Override
    public void list(String eventId, Callback callback) {
        List<Entrant> copy;
        synchronized (InMemoryStore.waiting) {
            copy = new ArrayList<>(InMemoryStore.bucket(InMemoryStore.waiting, eventId));
        }
        callback.onSuccess(copy);
    }

    @Override
    public List<Entrant> cached(String eventId) {
        synchronized (InMemoryStore.waiting) {
            return new ArrayList<>(InMemoryStore.bucket(InMemoryStore.waiting, eventId));
        }
    }

    @Override
    public int cachedCount(String eventId) {
        synchronized (InMemoryStore.waiting) {
            return InMemoryStore.bucket(InMemoryStore.waiting, eventId).size();
        }
    }
    // These helpers may NOT be declared in your interface; keep them without @Override
    public void add(String eventId, Entrant e) {
        synchronized (InMemoryStore.waiting) {
            InMemoryStore.bucket(InMemoryStore.waiting, eventId).add(e);
        }
    }

    public void remove(String eventId, Entrant e) {
        synchronized (InMemoryStore.waiting) {
            InMemoryStore.bucket(InMemoryStore.waiting, eventId).remove(e);
        }
    }
}
