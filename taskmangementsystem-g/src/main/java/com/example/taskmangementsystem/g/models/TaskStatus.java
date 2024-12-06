package com.example.taskmangementsystem.g.models;

public enum TaskStatus {
    TODO("todo"),
    PENDING("pending"),
    ON_HOLD("on_hold"),
    DONE("done"),
    IN_TEST("in_test"),
    IN_PROGRESS("in_progress"); // Added PROGRESS status

    private final String status;

    TaskStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
