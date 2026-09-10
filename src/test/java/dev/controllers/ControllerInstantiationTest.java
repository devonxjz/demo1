package dev.controllers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ControllerInstantiationTest {

    @Test
    void testServletsInstantiation() {
        DownloadServlet downloadServlet = new DownloadServlet();
        assertNotNull(downloadServlet);

        LoginServlet loginServlet = new LoginServlet();
        assertNotNull(loginServlet);

        RegisterServlet registerServlet = new RegisterServlet();
        assertNotNull(registerServlet);

        LogoutServlet logoutServlet = new LogoutServlet();
        assertNotNull(logoutServlet);
    }
}
