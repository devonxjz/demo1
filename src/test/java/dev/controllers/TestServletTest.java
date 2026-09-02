package dev.controllers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TestServletTest {

    @Test
    void testServletInstantiation() {
        TestServlet servlet = new TestServlet();
        assertNotNull(servlet);
    }
}
