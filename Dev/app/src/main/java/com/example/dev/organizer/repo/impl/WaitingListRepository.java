package com.example.dev.organizer.repo.impl;

import com.example.dev.organizer.Entrant;
import java.util.List;

public interface WaitingListRepository {
    List<Entrant> list(String eventId);
    int count(String eventId);
    void add(String eventId, Entrant e);
    void remove(String eventId, Entrant e);
}