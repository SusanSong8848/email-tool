package com.student.emailtool.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailValidatorTest {
    @Test
    void shouldAcceptValidAddress() {
        assertTrue(EmailValidator.isValid("alice123@example.com"));
    }

    @Test
    void shouldRejectMultipleAt() {
        assertFalse(EmailValidator.isValid("a@@example.com"));
    }

    @Test
    void shouldRejectDotBeforeAt() {
        assertFalse(EmailValidator.isValid("alice.test@example.com"));
    }

    @Test
    void shouldRejectInvalidDomain() {
        assertFalse(EmailValidator.isValid("alice@example..com"));
    }
}
