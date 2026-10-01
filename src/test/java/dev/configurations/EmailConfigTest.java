package dev.configurations;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class EmailConfigTest {

    @Test
    void testDefaultConfigLoad() {
        EmailConfig config = EmailConfig.getInstance();
        assertNotNull(config);
        assertNotNull(config.getHost());
        assertTrue(config.getPort() > 0);
        assertNotNull(config.getFrom());
        assertNotNull(config.getUsername());
        assertNotNull(config.getPassword());
    }

    @Test
    void testCustomPropertiesLoad() {
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", "smtp.mailtrap.io");
        props.setProperty("mail.smtp.port", "2525");
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.starttls.enable", "false");
        props.setProperty("mail.smtp.username", "testuser");
        props.setProperty("mail.smtp.password", "testpass");
        props.setProperty("mail.from", "orders@test.com");
        props.setProperty("mail.enabled", "true");

        EmailConfig config = new EmailConfig(props);
        assertEquals("smtp.mailtrap.io", config.getHost());
        assertEquals(2525, config.getPort());
        assertTrue(config.isAuth());
        assertFalse(config.isStarttls());
        assertEquals("testuser", config.getUsername());
        assertEquals("testpass", config.getPassword());
        assertEquals("orders@test.com", config.getFrom());
        assertTrue(config.isEnabled());
        assertTrue(config.isReadyToSend());
    }
}
