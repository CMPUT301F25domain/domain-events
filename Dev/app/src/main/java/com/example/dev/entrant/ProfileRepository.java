package com.example.dev.entrant;

import com.example.dev.entrant.Profile;
import java.util.List;
import java.util.Optional;

public interface ProfileRepository {
    Profile save(Profile profile);
    Optional<Profile> findById(String id);
    List<Profile> findAll();
    void deleteSoft(String id);
}
