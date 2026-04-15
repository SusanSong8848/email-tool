package com.student.emailtool.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContactTest {
    @Test
    void shouldOutputFormalCsvLine() {
        Contact contact = new Contact("aLiCe smith", "Alice.Smith@Example.COM", "nEw yOrK");

        String csvLine = contact.toCsvLine("formal");

        assertEquals("Alice Smith,alice.smith@example.com,New York", csvLine);
    }

    @Test
    void shouldOutputLowerCsvLine() {
        Contact contact = new Contact("ALICE Smith", "Alice.Smith@Example.COM", "NEW YORK");

        String csvLine = contact.toCsvLine("lower");

        assertEquals("alice smith,alice.smith@example.com,new york", csvLine);
    }
}
