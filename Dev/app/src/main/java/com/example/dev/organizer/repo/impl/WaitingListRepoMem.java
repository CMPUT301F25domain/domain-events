package com.example.dev.organizer.repo.impl;

import java.util.*;
import com.example.dev.repo.WaitingListRepository;
import com.example.dev.organizer.Entrant;

public class WaitingListRepoMem implements WaitingListRepository {
    @Override public List<Entrant> list(String eventId) {
        List<Entrant> src = InMemoryStore.i().waitingByEvent.getOrDefault(eventId, new ArrayList<>());
        src.sort(Comparator.comparingLong(e -> e.joinedAtMillis));
        return new ArrayList<>(src);
    }
    @Override public int count(String eventId) {
        return InMemoryStore.i().waitingByEvent.getOrDefault(eventId, new ArrayList<>()).size();
    }
}
