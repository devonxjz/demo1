package dev;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmailListServletTest {
    @Test
    void normalizesValidInputAndRejectsInvalidInput() {
        assertNull(EmailListServlet.subscriber("an@example.com", "", "Nguyen"));
        assertNull(EmailListServlet.subscriber("not-an-email", "An", "Nguyen"));

        EmailListServlet.Subscriber subscriber = EmailListServlet.subscriber(
                "  an@example.com  ",
                "  An  ",
                "  Nguyen  ");

        assertEquals("an@example.com", subscriber.email());
        assertEquals("An", subscriber.firstName());
        assertEquals("Nguyen", subscriber.lastName());
    }

    @Test
    void escapesHtmlBeforeRenderingUserInput() {
        assertEquals("&lt;script&gt;&amp;&quot;&#39;&lt;/script&gt;",
                EmailListServlet.escapeHtml("<script>&\"'</script>"));
    }
}
