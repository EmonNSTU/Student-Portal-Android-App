package com.example.studentportal;

public class BatchModelClass {

    private String batch, session;

    BatchModelClass(String batch, String session) {
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
