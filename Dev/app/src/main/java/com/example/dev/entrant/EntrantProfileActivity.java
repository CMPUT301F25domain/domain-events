package com.example.dev.entrant;


import com.example.dev.entrant.Profile;
import com.example.dev.entrant.ProfileRepository;
import java.util.Optional;

public final class EntrantProfileActivity {
    private final ProfileRepository repo;
    public EntrantProfileActivity(ProfileRepository repo) { this.repo = repo; }

    /** Create profile (US 01.02.01) */
    public Profile create(String name, String email, String phone) {
        Profile p = Profile.newProfile(name, email, phone);
        return repo.save(p);
    }

    /** Update profile (US 01.02.02) */
    public Profile update(ProfileUpdateRequest req) {
        Profile existing = repo.findById(req.id)
                .orElseThrow(() -> new IllegalArgumentException("profile not found"));
        req.name.ifPresent(existing::setName);
        req.email.ifPresent(existing::setEmail);
        if (req.phone != null) req.phone.ifPresent(existing::setPhone);
        return repo.save(existing);
    }

    /** Soft delete (US 01.02.04) */
    public void softDelete(String id) {
        Profile p = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("profile not found"));
        if (!p.isDeleted()) {
            p.markDeleted(true);
            repo.save(p);
        }
    }

    public Optional<Profile> get(String id) { return repo.findById(id); }
    public boolean isUsable(String id) { return repo.findById(id).map(p -> !p.isDeleted()).orElse(false); }
}
