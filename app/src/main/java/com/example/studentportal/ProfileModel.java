package com.example.studentportal;

public class ProfileModel {
    public String userName;
    public String userAge;
    public String session;

    public ProfileModel() {}

    public ProfileModel(String userName, String userAge, String session) {
        this.userName = userName;
        this.userAge = userAge;
        this.session = session;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }
}
