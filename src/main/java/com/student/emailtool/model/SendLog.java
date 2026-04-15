package com.student.emailtool.model;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class SendLog {
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    private final Instant timestamp;
    private final String recipientEmail;
    private final String subject;
    private final String status;
    private final String failureReason;

    public SendLog(Instant timestamp,
                   String recipientEmail,
                   String subject,
                   String status,
                   String failureReason) {
        this.timestamp = timestamp;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.status = status;
        this.failureReason = failureReason;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public String getSubject() {
        return subject;
    }

    public String getStatus() {
        return status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public String toCsvLine() {
        return escapeCsv(ISO_FORMATTER.format(timestamp)) + ","
                + escapeCsv(recipientEmail) + ","
                + escapeCsv(subject) + ","
                + escapeCsv(status) + ","
                + escapeCsv(failureReason == null ? "" : failureReason);
    }

    private static String escapeCsv(String value) {
        String safe = value == null ? "" : value;
        boolean needQuote = safe.contains(",") || safe.contains("\"") || safe.contains("\n") || safe.contains("\r");
        if (!needQuote) {
            return safe;
        }
        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }
}
