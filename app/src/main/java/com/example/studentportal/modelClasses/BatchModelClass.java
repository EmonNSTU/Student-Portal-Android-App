package com.example.studentportal.modelClasses;

public class BatchModelClass {

    private String batch, session;

    public BatchModelClass(String batch, String session) {
        this.batch = batch;
        this.session = session;
    }

    public String getBatch() {
        return batch;
    }

    public String getSession() {
        return session;
    }
}
