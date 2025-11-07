package com.example.dev.repo;

import java.util.List;
import com.example.dev.organizer.Enrollment;

public interface EnrollmentRepository {
    List<Enrollment> list(String eventId);
    void enroll(String eventId, String entrantId);
    void remove(String eventId, String entrantId);
}
