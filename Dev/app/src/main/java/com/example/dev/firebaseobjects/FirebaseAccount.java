package com.example.dev.firebaseobjects;

import java.util.Collections;
import java.util.Set;

public class FirebaseAccount {
    private String accountID;
    private int accountType;
    private String password;
    private String username;
    private Set<String> wishListEvents;

    public FirebaseAccount(String accountID,
                           int accountType,
                           String password,
                           String username) {
        this.accountID = accountID;
        this.accountType = accountType;
        this.password = password;
        this.username = username;
        this.wishListEvents = Collections.<String> emptySet();
    }

    public String getAccountID() {
        return accountID;
    }
    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public int getAccountType() {
        return accountType;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public boolean existsInWishListEvents(FirebaseEvent event) {
        String eventID = event.getEventId();
        return wishListEvents.contains(eventID);
    }
    public void addToWishListEvents(FirebaseEvent event) {
        String eventID = event.getEventId();
        wishListEvents.add(eventID);
    }
    public void removeFromWishListEvents(FirebaseEvent event) {
        String eventID = event.getEventId();
        wishListEvents.remove(eventID);
    }
}
