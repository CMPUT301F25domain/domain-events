package com.example.dev.organizer;

public class Invitation {
    public final String id;
    public final String eventId;
    public final String entrantId;
    public InvitationStatus status;
    public final long issuedAtMillis;

    public Invitation(String id, String eventId, String entrantId,
                      InvitationStatus status, long issuedAtMillis) {
        this.id = id; this.eventId = eventId; this.entrantId = entrantId;
        this.status = status; this.issuedAtMillis = issuedAtMillis;
    }
}
