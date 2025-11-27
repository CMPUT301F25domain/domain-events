package com.example.dev.organizer.repo.impl;

import com.example.dev.organizer.Entrant;
import com.example.dev.repo.WaitingListRepository;

import java.util.ArrayList;
import java.util.List;

/** In-memory waiting list backed by shared InMemoryStore. */
public class WaitingListRepoMem implements WaitingListRepository {

    @Override
    public List<Entrant> list(String eventId) {
        synchronized (InMemoryStore.waiting) {
            return new ArrayList<>(InMemoryStore.bucket(InMemoryStore.waiting, eventId));
        }
    }

    @Override
    public int count(String eventId) {
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
