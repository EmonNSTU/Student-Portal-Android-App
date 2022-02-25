package com.example.studentportal;

public class StudentModelClass {

    private String name, roll, userId;

    StudentModelClass(String name, String id, String userId) {
        this.name = name;
        this.roll = id;
        this.userId = userId;
    }

    public String getName() {

        return name;
    }

    public String getRoll() {
        return roll;
    }
    public String getUserId() {
        return userId;
    }

}
