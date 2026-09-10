package dev.controllers;

import dev.utils.CookieUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processLogout(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processLogout(request, response);
    }

    private void processLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("user");
            session.invalidate();
        }

        // Xóa cookie người dùng
        CookieUtil.deleteCookie(response, CookieUtil.USER_COOKIE_NAME);
        CookieUtil.deleteCookie(response, CookieUtil.USER_OBJECT_COOKIE_NAME);

        response.sendRedirect(request.getContextPath() + "/login?loggedOut=true");
    }
}
