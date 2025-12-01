package com.example.dev.entrant;
import java.util.Optional;

public final class ProfileUpdateRequest {
    public final String id;
    public final Optional<String> name;
    public final Optional<String> email;
    public final Optional<String> phone;

    public ProfileUpdateRequest(String id, Optional<String> name,
                                Optional<String> email, Optional<String> phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}