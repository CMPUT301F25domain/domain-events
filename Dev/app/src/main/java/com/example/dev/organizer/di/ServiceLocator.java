package com.example.dev.organizer.di;

import com.example.dev.repo.WaitingListRepository;
import com.example.dev.repo.InvitationRepository;
import com.example.dev.organizer.repo.impl.*;

public final class ServiceLocator {
    private static final WaitingListRepository WAITING = new WaitingListRepoMem();
    private static final LotteryRepoMem LOTTERY = new LotteryRepoMem();
    private static final InvitationRepository INVITES = new InvitationRepoMem();
    private static final EnrollmentRepoMem ENROLL = new EnrollmentRepoMem();
    private static final NotificationRepoMem NOTIFY = new NotificationRepoMem();

    private ServiceLocator() {}

    public static WaitingListRepository waiting() { return WAITING; }
    public static LotteryRepoMem lottery() { return LOTTERY; }
    public static InvitationRepository invites() { return INVITES; }

    // Provide BOTH names so existing code compiles
    public static EnrollmentRepoMem enrollment() { return ENROLL; }
    public static EnrollmentRepoMem enroll() { return ENROLL; }

    public static NotificationRepoMem notifications() { return NOTIFY; }
    public static NotificationRepoMem notif() { return NOTIFY; }
}
