package com.student.emailtool.model;

import java.time.Instant;

public class SendLog {
    private final String email;
    private final boolean success;
    private final String message;
    private final Instant sentAt;

    public SendLog(String email, boolean success, String message, Instant sentAt) {
        this.email = email;
        this.success = success;
        this.message = message;
        this.sentAt = sentAt;
    }

    public String getEmail() {
        return email;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Instant getSentAt() {
        return sentAt;
    }
}
