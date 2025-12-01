package com.example.dev.repo;

import com.example.dev.organizer.Entrant;
import java.util.List;

public interface WaitingListRepository {
    interface Callback {
        void onSuccess(List<Entrant> entrants);
        void onError(Exception e);
    }

    /**
     * Fetch the waiting list for the given event. Implementations may return cached results
     * immediately and then invoke the callback again when fresh data arrives.
     */
    void list(String eventId, Callback callback);

    /** Latest cached entrants for the given event, or an empty list if none are cached. */
    List<Entrant> cached(String eventId);

    /** Cached count for the given event. */
    int cachedCount(String eventId);
}