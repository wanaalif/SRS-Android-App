package com.example.srsapplication;

public class User {
    private String fullName;
    private String email;
    private String contact;

    public User(String fullName, String email, String contact) {
        this.fullName = fullName;
        this.email = email;
        this.contact = contact;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }
}
