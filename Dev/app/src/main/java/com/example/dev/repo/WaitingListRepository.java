package com.example.dev.repo;

import java.util.List;

import com.example.dev.organizer.Entrant;

public interface WaitingListRepository {
    List<Entrant> list(String eventId);
    int count(String eventId);
}