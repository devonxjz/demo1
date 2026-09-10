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

    @Test
    void userAuthConstructorAndDisplayName() {
        User user1 = new User("devon", "devon@example.com", "bcryptHash", "Nam", "Nguyen");
        assertEquals("devon", user1.getUsername());
        assertEquals("devon@example.com", user1.getEmail());
        assertEquals("bcryptHash", user1.getPassword());
        assertEquals("Nam", user1.getDisplayName());

        User user2 = new User("devon", "devon@example.com", "bcryptHash", "", "");
        assertEquals("devon", user2.getDisplayName());

        User user3 = new User("", "devon@example.com", "bcryptHash", "", "");
        assertEquals("devon@example.com", user3.getDisplayName());

        User user4 = new User("", "", "bcryptHash", "", "");
        assertEquals("User", user4.getDisplayName());

        User user5 = new User(null, null, null, null, null);
        assertEquals("User", user5.getDisplayName());
        assertEquals("", user5.getUsername());
    }
}
