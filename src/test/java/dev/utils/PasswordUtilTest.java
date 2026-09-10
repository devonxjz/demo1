package dev.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void testHashAndCheckPasswordSuccess() {
        String rawPassword = "mySecurePassword123!";
        String hashed = PasswordUtil.hashPassword(rawPassword);

        assertNotNull(hashed);
        assertTrue(hashed.startsWith("$2a$") || hashed.startsWith("$2y$") || hashed.startsWith("$2b$"));
        assertTrue(PasswordUtil.checkPassword(rawPassword, hashed));
    }

    @Test
    void testCheckPasswordFailureWithWrongPassword() {
        String rawPassword = "correctPassword";
        String wrongPassword = "wrongPassword";
        String hashed = PasswordUtil.hashPassword(rawPassword);

        assertFalse(PasswordUtil.checkPassword(wrongPassword, hashed));
    }

    @Test
    void testCheckPasswordHandlesNullsGracefully() {
        assertFalse(PasswordUtil.checkPassword(null, "hash"));
        assertFalse(PasswordUtil.checkPassword("raw", null));
        assertFalse(PasswordUtil.checkPassword("raw", "invalid_hash"));
    }
}
