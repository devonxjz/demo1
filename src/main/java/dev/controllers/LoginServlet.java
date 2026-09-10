package dev.controllers;

import dev.models.User;
import dev.services.AuthService;
import dev.utils.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Nếu người dùng đã đăng nhập, chuyển về trang chủ hoặc order
        if (request.getSession().getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/order");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String identifier = request.getParameter("identifier");
        String password = request.getParameter("password");

        if (identifier == null || identifier.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ Tên đăng nhập/Email và Mật khẩu.");
            request.setAttribute("identifier", identifier);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        Optional<User> userOpt = authService.login(identifier, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Lưu session và cookie
            CookieUtil.syncUser(request, response, user);

            String redirectUrl = request.getParameter("redirect");
            if (redirectUrl != null && !redirectUrl.trim().isEmpty() && !redirectUrl.contains("login")) {
                response.sendRedirect(redirectUrl);
            } else {
                response.sendRedirect(request.getContextPath() + "/order");
            }
        } else {
            request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác.");
            request.setAttribute("identifier", identifier);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}
