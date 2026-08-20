package dev;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LoginServletTest {
    @Test
    void normalizesValidInputAndRejectsInvalidInput() {
        assertNull(LoginServlet.credentials("", "an@example.com"));
        assertNull(LoginServlet.credentials("An", "not-an-email"));

        LoginServlet.Credentials credentials = LoginServlet.credentials("  An  ", "  an@example.com  ");

        assertEquals("An", credentials.username());
        assertEquals("an@example.com", credentials.email());
    }

    @Test
    void escapesHtmlBeforeRenderingUserInput() {
        assertEquals("&lt;script&gt;&amp;&quot;&#39;&lt;/script&gt;",
                LoginServlet.escapeHtml("<script>&\"'</script>"));
    }
}
