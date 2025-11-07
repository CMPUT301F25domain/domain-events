package com.example.dev.entrant;

import com.example.dev.entrant.Profile;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryProfileRepository implements ProfileRepository {
    private final Map<String, Profile> store = new ConcurrentHashMap<>();

    @Override
    public Profile save(Profile profile) {
        store.put(profile.getId(), profile);
        return profile;
    }

    @Override
    public Optional<Profile> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Profile> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteSoft(String id) {
        Profile p = store.get(id);
        if (p != null) {
            p.markDeleted(true);
            store.put(id, p);
        }
    }
}

