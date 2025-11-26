package com.example.dev.repo;

import com.example.dev.organizer.Invitation;
import com.example.dev.organizer.InvitationStatus;
import java.util.List;

public interface InvitationRepository {
    List<Invitation> listByEvent(String eventId);
    Invitation invite(String eventId, String entrantId);
    void setStatus(String invitationId, InvitationStatus status);
    Invitation get(String invitationId);
}
