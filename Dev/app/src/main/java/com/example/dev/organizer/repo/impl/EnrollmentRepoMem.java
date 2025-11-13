package com.example.dev.organizer.repo.impl;

import java.util.ArrayList;
import java.util.List;

import com.example.dev.repo.EnrollmentRepository;
import com.example.dev.organizer.Enrollment;

public class EnrollmentRepoMem implements EnrollmentRepository {
    @Override public List<Enrollment> list(String eventId) {
        return new ArrayList<>(InMemoryStore.i().enrollmentsByEvent.getOrDefault(eventId, new ArrayList<>()));
    }
    @Override public void enroll(String eventId, String entrantId) {
        InMemoryStore.i().enrollmentsByEvent.computeIfAbsent(eventId,k->new ArrayList<>())
                .add(new Enrollment(eventId, entrantId, System.currentTimeMillis()));
        InMemoryStore.i().recomputeQueue(eventId);
    }
    @Override public void remove(String eventId, String entrantId) {
        List<Enrollment> list = InMemoryStore.i().enrollmentsByEvent.getOrDefault(eventId, new ArrayList<>());
        list.removeIf(e -> e.entrantId.equals(entrantId));
        InMemoryStore.i().recomputeQueue(eventId);
    }
}
