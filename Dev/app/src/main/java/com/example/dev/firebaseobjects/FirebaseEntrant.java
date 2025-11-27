package com.example.dev.firebaseobjects;

public class FirebaseEntrant {
    private String name;
    private String email;

    public FirebaseEntrant() {}

    public FirebaseEntrant(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
}
