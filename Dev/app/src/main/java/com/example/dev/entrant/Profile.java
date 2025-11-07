package com.example.dev.entrant;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Profile {
    private final String id;
    private String name;
    private String email;
    private String phone;
    private final Instant createdAt;
    private Instant updatedAt;
    private boolean deleted;

    private Profile(String id, String name, String email, String phone,
                    Instant createdAt, Instant updatedAt, boolean deleted) {
        this.id = Objects.requireNonNull(id);
        setName(name);
        setEmail(email);
        setPhone(phone);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
        this.deleted = deleted;
    }

    public static Profile newProfile(String name, String email, String phone) {
        Instant now = Instant.now();
        return new Profile(UUID.randomUUID().toString(), name, email, phone, now, now, false);
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("name required");
        this.name = name.trim();
        touch();
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
            throw new IllegalArgumentException("invalid email");
        this.email = email.trim();
        touch();
    }

    public void setPhone(String phone) {
        if (phone != null && phone.trim().isEmpty()) phone = null;
        this.phone = phone;
        touch();
    }

    public void markDeleted(boolean value) { this.deleted = value; touch(); }
    private void touch() { this.updatedAt = Instant.now(); }

    // getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public boolean isDeleted() { return deleted; }
}

