package com.example.dev;

public class FirebaseAccount {
    private String accountID;
    private int accountType;
    private String password;
    private String username;

    public FirebaseAccount(String accountID,
                           int accountType,
                           String password,
                           String username) {
        this.accountID = accountID;
        this.accountType = accountType;
        this.password = password;
        this.username = username;
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
}
