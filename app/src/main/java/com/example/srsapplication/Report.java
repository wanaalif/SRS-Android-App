package com.example.srsapplication;

public class Report {
    private String name;
    private String phoneNumber;
    private String location;
    private String date;
    private String description;

    public Report() {
        // Default constructor required for Firebase
    }

    public Report(String name, String phoneNumber, String location, String date, String description) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.date = date;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
