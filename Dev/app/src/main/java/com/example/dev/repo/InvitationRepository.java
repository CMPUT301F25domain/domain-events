package com.example.dev.repo;

import java.util.List;
import com.example.dev.organizer.Invitation;
import com.example.dev.organizer.InvitationStatus;

public interface InvitationRepository {
    List<Invitation> listByEvent(String eventId);
    void setStatus(String invitationId, InvitationStatus status);
    Invitation invite(String eventId, String entrantId); // creates INVITED
}
