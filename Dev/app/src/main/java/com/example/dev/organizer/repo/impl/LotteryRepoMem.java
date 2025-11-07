package com.example.dev.organizer.repo.impl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.example.dev.repo.LotteryRepository;

public class LotteryRepoMem implements LotteryRepository {
    @Override public List<String> nextUpOrder(String eventId) {
        return new ArrayList<>(InMemoryStore.i().replacementQueueByEvent.getOrDefault(eventId, new ArrayDeque<>()));
    }

    @Override public String replaceWithNext(String eventId) {
        Queue<String> q = InMemoryStore.i().replacementQueueByEvent.get(eventId);
        if (q == null || q.isEmpty()) return null;
        String nextId = q.poll();
        InMemoryStore.i().recomputeQueue(eventId);
        return nextId;
    }
}
