package com.example.dev.organizer.repo.impl;

import com.example.dev.organizer.Invitation;
import com.example.dev.organizer.InvitationStatus;
import com.example.dev.repo.InvitationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory Invitation repo for demo/testing.
 * Matches InvitationRepository:
 *  - listByEvent(String)
 *  - invite(String, String)
 *  - setStatus(String, InvitationStatus)
 *  - get(String)
 */
public class InvitationRepoMem implements InvitationRepository {

    // id -> invitation
    private final Map<String, Invitation> byId = new ConcurrentHashMap<>();

    @Override
    public List<Invitation> listByEvent(String eventId) {
        synchronized (InMemoryStore.invitations) {
            return new ArrayList<>(InMemoryStore.bucket(InMemoryStore.invitations, eventId));
        }
    }

    @Override
    public Invitation invite(String eventId, String entrantId) {
        String id = UUID.randomUUID().toString();
        // 5th parameter: createdAtMillis (or whatever your Invitation’s 5th field is)
        Invitation inv = new Invitation(
                id,
                eventId,
                entrantId,
                InvitationStatus.INVITED,
                System.currentTimeMillis()   // <-- 5th arg
        );

        byId.put(id, inv);
        synchronized (InMemoryStore.invitations) {
            InMemoryStore.bucket(InMemoryStore.invitations, eventId).add(inv);
        }
        return inv;
    }

    @Override
    public void setStatus(String invitationId, InvitationStatus status) {
        Invitation inv = byId.get(invitationId);
        if (inv != null) {
            inv.status = status;
        }
    }

    @Override
    public Invitation get(String invitationId) {
        return byId.get(invitationId);
    }
}
