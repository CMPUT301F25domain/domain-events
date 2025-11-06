package com.example.dev.organizer.repo.impl;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import com.example.dev.repo.InvitationRepository;
import com.example.dev.organizer.Invitation;
import com.example.dev.organizer.InvitationStatus;

public class InvitationRepoMem implements InvitationRepository {
    @Override public List<Invitation> listByEvent(String eventId) {
        return new ArrayList<>(InMemoryStore.i().invitesByEvent.getOrDefault(eventId, new ArrayList<>()));
    }

    @Override public void setStatus(String invitationId, InvitationStatus status) {
        for (List<Invitation> list : InMemoryStore.i().invitesByEvent.values()) {
            for (Invitation inv : list) {
                if (inv.id.equals(invitationId)) {
                    inv.status = status;
                    return;
                }
            }
        }
    }

    @Override public Invitation invite(String eventId, String entrantId) {
        String id = UUID.randomUUID().toString();
        Invitation inv = new Invitation(id, eventId, entrantId, InvitationStatus.INVITED, System.currentTimeMillis());
        InMemoryStore.i().invitesByEvent.computeIfAbsent(eventId, k -> new ArrayList<>()).add(inv);
        return inv;
    }
}
