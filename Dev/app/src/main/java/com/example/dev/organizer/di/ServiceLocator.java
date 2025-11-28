package com.example.dev.organizer.di;

import com.example.dev.repo.*;
import com.example.dev.organizer.repo.impl.*;

public final class ServiceLocator {
    private static final WaitingListRepository waiting = new WaitingListRepoMem();
    private static final InvitationRepository invites = new InvitationRepoMem();
    private static final EnrollmentRepository enroll = new EnrollmentRepoMem();
    private static final LotteryRepository lottery = new LotteryRepoMem();
    private static final NotificationRepository notif = new NotificationRepoMem();

    public static WaitingListRepository waiting() { return waiting; }
    public static InvitationRepository invites() { return invites; }
    public static EnrollmentRepository enroll() { return enroll; }
    public static LotteryRepository lottery() { return lottery; }
    public static NotificationRepository notif() { return notif; }
}
