package com.example.dev.repo;

import java.util.List;

public interface LotteryRepository {
    /** Order of remaining eligible entrants for replacement (frozen at close). */
    List<String> nextUpOrder(String eventId);
    /** Replace a cancelled/declined invitee with the next eligible waiting-list entrant. */
    String replaceWithNext(String eventId); // returns new entrantId or null
}

