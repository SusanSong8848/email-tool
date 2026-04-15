package com.student.emailtool.util;

public final class EmailValidator {
    private EmailValidator() {
    }

    public static boolean isValid(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        if (!email.matches("[a-zA-Z0-9@.]+")) {
            return false;
        }

        int atIndex = email.indexOf('@');
        if (atIndex == -1 || atIndex != email.lastIndexOf('@')) {
            return false;
        }
        if (atIndex == 0 || atIndex == email.length() - 1) {
            return false;
        }

        String local = email.substring(0, atIndex);
        String domain = email.substring(atIndex + 1);

        if (local.contains(".")) {
            return false;
        }
        if (!domain.contains(".")) {
            return false;
        }
        if (domain.startsWith(".") || domain.endsWith(".")) {
            return false;
        }
        if (domain.contains("..")) {
            return false;
        }

        String[] domainParts = domain.split("\\.");
        for (String part : domainParts) {
            if (part.isEmpty()) {
                return false;
            }
            for (char c : part.toCharArray()) {
                if (!Character.isLetterOrDigit(c)) {
                    return false;
                }
            }
        }
        return true;
    }
}
