package dev;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@WebServlet("/login")
public final class EmailListServlet extends HttpServlet {
    private static final int MAX_NAME_LENGTH = 50;
    private static final int MAX_EMAIL_LENGTH = 254;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/html");
        response.setHeader("Content-Security-Policy",
                "default-src 'none'; style-src 'self'; form-action 'self'; base-uri 'none'; frame-ancestors 'none'");
        response.setHeader("X-Content-Type-Options", "nosniff");

        Subscriber input = subscriber(
                request.getParameter("email"),
                request.getParameter("fname"),
                request.getParameter("lname"));
        if (input == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writePage(response, "Thông tin không hợp lệ",
                    "<h1>Thông tin không hợp lệ</h1><a class=\"button\" href=\"/\">Thử lại</a>");
            return;
        }

        writePage(response, "Join email list",
                "<h1>Thank you " + escapeHtml(input.firstName()) + " " + escapeHtml(input.lastName()) + "</h1>"
                        + "<p>Email: " + escapeHtml(input.email()) + "</p>"
                        + "<a class=\"button\" href=\"/\">Quay lại</a>");
    }

    static Subscriber subscriber(String email, String firstName, String lastName) {
        if (email == null || firstName == null || lastName == null) return null;

        String normalizedEmail = email.trim();
        String normalizedFirstName = firstName.trim();
        String normalizedLastName = lastName.trim();
        if (normalizedEmail.length() > MAX_EMAIL_LENGTH || !EMAIL_PATTERN.matcher(normalizedEmail).matches()) return null;
        if (normalizedFirstName.isEmpty() || normalizedFirstName.length() > MAX_NAME_LENGTH) return null;
        if (normalizedLastName.isEmpty() || normalizedLastName.length() > MAX_NAME_LENGTH) return null;
        return new Subscriber(normalizedEmail, normalizedFirstName, normalizedLastName);
    }

    static String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static void writePage(HttpServletResponse response, String title, String content) throws IOException {
        response.getWriter().write("""
                <!doctype html>
                <html lang="vi">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width,initial-scale=1">
                  <title>%s</title>
                  <link rel="stylesheet" href="/style.css">
                </head>
                <body><main class="card">%s</main></body>
                </html>
                """.formatted(title, content));
    }

    record Subscriber(String email, String firstName, String lastName) {
    }
}
