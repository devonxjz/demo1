package dev.controllers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SurveyServletTest {

    @Test
    void testServletInstantiation() {
        SurveyServlet servlet = new SurveyServlet();
        assertNotNull(servlet);
    }
}
