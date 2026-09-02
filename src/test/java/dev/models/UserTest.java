package dev.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    @Test
    void userBeanCorrectlyProcessesFormData() {
        User user = new User(
                " John ",
                " Doe ",
                " john.doe@example.com ",
                " 01/01/2000 ",
                "Search engine",
                "YES, I'd like that.",
                null,
                "Email only"
        );

        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("01/01/2000", user.getDateOfBirth());
        assertEquals("Search engine", user.getHeardFrom());
        assertEquals("Yes", user.getWantsUpdates());
        assertEquals("No", user.getEmailAnnouncements());
        assertEquals("Email only", user.getContactBy());
    }

    @Test
    void userBeanHandlesNullAndEmptyValues() {
        User user = new User(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertEquals("", user.getFirstName());
        assertEquals("", user.getLastName());
        assertEquals("", user.getEmail());
        assertEquals("", user.getDateOfBirth());
        assertEquals("Not specified", user.getHeardFrom());
        assertEquals("No", user.getWantsUpdates());
        assertEquals("No", user.getEmailAnnouncements());
        assertEquals("Email or postal mail", user.getContactBy());
    }
}
