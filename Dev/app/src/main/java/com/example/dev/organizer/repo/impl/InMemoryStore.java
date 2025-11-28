package com.example.dev.organizer.repo.impl;

import java.util.*;
import com.example.dev.Keys;
import com.example.dev.organizer.Entrant;
import com.example.dev.organizer.Invitation;
import com.example.dev.organizer.InvitationStatus;
import com.example.dev.organizer.Enrollment;

public final class InMemoryStore {
    private static final InMemoryStore I = new InMemoryStore();
    public static InMemoryStore i() { return I; }

    public final Map<String, List<Entrant>> waitingByEvent = new HashMap<>();
    public final Map<String, List<Invitation>> invitesByEvent = new HashMap<>();
    public final Map<String, List<Enrollment>> enrollmentsByEvent = new HashMap<>();
    public final Map<String, List<String>> notifLogByEvent = new HashMap<>();
    public final Map<String, Queue<String>> replacementQueueByEvent = new HashMap<>();

    private InMemoryStore() {
        // seed demo data
        List<Entrant> waiting = new ArrayList<>();
        waiting.add(new Entrant("u1","Farzan Reza","farzan@ex.com", System.currentTimeMillis()-800000,"NW"));
        waiting.add(new Entrant("u2","Jane Porter","jane@ex.com", System.currentTimeMillis()-700000,"NE"));
        waiting.add(new Entrant("u3","John Claus","john@ex.com", System.currentTimeMillis()-600000,"SW"));
        waiting.add(new Entrant("u4","Rafi","rafi@ex.com", System.currentTimeMillis()-500000,"SE"));

        waitingByEvent.put(Keys.DEMO_EVENT_ID, waiting);

        List<Invitation> inv = new ArrayList<>();
        inv.add(new Invitation("i1", Keys.DEMO_EVENT_ID, "u1", InvitationStatus.INVITED, System.currentTimeMillis()-400000));
        inv.add(new Invitation("i2", Keys.DEMO_EVENT_ID, "u2", InvitationStatus.DECLINED, System.currentTimeMillis()-300000));
        invitesByEvent.put(Keys.DEMO_EVENT_ID, inv);


        List<Enrollment> en = new ArrayList<>();
        en.add(new Enrollment(Keys.DEMO_EVENT_ID, "u1", System.currentTimeMillis()-200000)); // pretend accepted
        enrollmentsByEvent.put(Keys.DEMO_EVENT_ID, en);

        recomputeQueue(Keys.DEMO_EVENT_ID);
    }

    public void recomputeQueue(String eventId) {
        Set<String> inPipeline = new HashSet<>();
        for (Invitation it : invitesByEvent.getOrDefault(eventId, Collections.emptyList())) {
            inPipeline.add(it.entrantId);
        }
        for (Enrollment e : enrollmentsByEvent.getOrDefault(eventId, Collections.emptyList())) {
            inPipeline.add(e.entrantId);
        }
        Queue<String> q = new ArrayDeque<>();
        for (Entrant e : waitingByEvent.getOrDefault(eventId, Collections.emptyList())) {
            if (!inPipeline.contains(e.id)) q.add(e.id);
        }
        replacementQueueByEvent.put(eventId, q);
    }
}
