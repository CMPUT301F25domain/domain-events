package com.example.dev.organizer.repo.impl;

import androidx.annotation.Nullable;

import com.example.dev.organizer.Entrant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * In-memory lottery operations for demo/testing.
 * - draw(eventId, n): randomly sample up to n entrants from waiting -> move them to invited.
 * - nextEligible(eventId): pop the next person from waiting (FIFO), move to invited, return their identifier.
 * - replaceWithNext(eventId): alias kept for backward compatibility.
 */
public class LotteryRepoMem {

    private final Random rnd = new Random();

    /** Randomly draw up to n entrants from WAITING and move them to INVITED. */
    public List<Entrant> draw(String eventId, int n) {
        // Snapshot waiting
        final List<Entrant> waitingSnapshot;
        synchronized (InMemoryStore.waiting) {
            waitingSnapshot = new ArrayList<>(InMemoryStore.bucket(InMemoryStore.waiting, eventId));
        }

        // Shuffle and take first n
        Collections.shuffle(waitingSnapshot, rnd);
        List<Entrant> drawn = waitingSnapshot;
        if (n < drawn.size()) {
            drawn = new ArrayList<>(drawn.subList(0, n));
        }

        // Move drawn from waiting -> invited
        synchronized (InMemoryStore.waiting) {
            InMemoryStore.bucket(InMemoryStore.waiting, eventId).removeAll(drawn);
        }
        synchronized (InMemoryStore.invited) {
            InMemoryStore.bucket(InMemoryStore.invited, eventId).addAll(drawn);
        }

        return drawn;
    }

    /**
     * FIFO replacement: take the earliest entrant still in WAITING,
     * move them to INVITED, and return a String identifier.
     *
     * NOTE: We return Entrant.email as the identifier because your current
     * code treats a String "entrantId" consistently as that value. If you later
     * add a dedicated Entrant.id, return that instead.
     */
    public @Nullable String nextEligible(String eventId) {
        Entrant next = null;

        // Pop from the head of the waiting list
        synchronized (InMemoryStore.waiting) {
            List<Entrant> q = InMemoryStore.bucket(InMemoryStore.waiting, eventId);
            if (!q.isEmpty()) {
                next = q.remove(0);
            }
        }
        if (next == null) return null;

        // Move to invited
        synchronized (InMemoryStore.invited) {
            InMemoryStore.bucket(InMemoryStore.invited, eventId).add(next);
        }

        // Return a stable identifier string used by your InvitationRepoMem.invite(...)
        return next.email; // TODO: switch to next.id if/when you add a real id
    }

    /** Backward-compat alias for branches still calling replaceWithNext(...). */
    public @Nullable String replaceWithNext(String eventId) {
        return nextEligible(eventId);
    }
}
