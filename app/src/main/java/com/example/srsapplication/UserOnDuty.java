package com.example.srsapplication;

public class UserOnDuty {
    private String date;
    private String name;
    private String contact;
    private String location;
    private String shift;
    private Boolean remark;

    public UserOnDuty(){}
    public UserOnDuty(String date, String fullName, String contact, String location, String shift, Boolean leader) {
        this.date = date;
        this.name = fullName;
        this.contact = contact;
        this.location = location;
        this.shift = shift;
        this.remark = leader;
    }

    public String getDate(){return date;}

    public String getName(){return name;}

    public String getContact(){return contact;}

    public String getLocation() {
        return location;
    }

    public String getShift() {
        return shift;
    }

    public Boolean getRemark() { return remark; }
}
