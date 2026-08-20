package dev;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@WebServlet("/login")
public final class LoginServlet extends HttpServlet {
    private static final int MAX_USERNAME_LENGTH = 50;
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

        Credentials input = credentials(request.getParameter("username"), request.getParameter("email"));
        if (input == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writePage(response, "Thông tin không hợp lệ",
                    "<h1>Thông tin không hợp lệ</h1><a class=\"button\" href=\"/\">Thử lại</a>");
            return;
        }

        writePage(response, "Xin chào",
                "<h1>Xin chào " + escapeHtml(input.username()) + "</h1>"
                        + "<p>Email: " + escapeHtml(input.email()) + "</p>"
                        + "<a class=\"button\" href=\"/\">Quay lại</a>");
    }

    static Credentials credentials(String username, String email) {
        if (username == null || email == null) return null;

        String normalizedUsername = username.trim();
        String normalizedEmail = email.trim();
        if (normalizedUsername.isEmpty() || normalizedUsername.length() > MAX_USERNAME_LENGTH) return null;
        if (normalizedEmail.length() > MAX_EMAIL_LENGTH || !EMAIL_PATTERN.matcher(normalizedEmail).matches()) return null;
        return new Credentials(normalizedUsername, normalizedEmail);
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

    record Credentials(String username, String email) {
    }
}
