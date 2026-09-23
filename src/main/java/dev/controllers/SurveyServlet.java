package dev.controllers;

import dev.dao.UserDao;
import dev.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/survey")
public class SurveyServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        if (email == null || email.trim().isEmpty()) {
            response.sendRedirect("index.jsp");
            return;
        }

        // 1. Lấy dữ liệu từ form
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String dateOfBirth = request.getParameter("dateOfBirth");
        String heardFrom = request.getParameter("heardFrom");
        String wantsUpdates = request.getParameter("wantsUpdates");
        String emailAnnouncements = request.getParameter("emailAnnouncements");
        String contactBy = request.getParameter("contactBy");

        // 2. Tạo đối tượng User
        User user = new User(
                firstName,
                lastName,
                email,
                dateOfBirth,
                heardFrom,
                wantsUpdates,
                emailAnnouncements,
                contactBy
        );

        // 3. Lưu thông qua tầng UserDao
        userDao.save(user);

        // 4. Đồng bộ User Object vào Cookie và Session
        dev.utils.CookieUtil.syncUser(request, response, user);

        // 5. Đưa đối tượng user vào request attribute
        request.setAttribute("user", user);

        // 6. Chuyển tiếp (forward) sang trang JSP hiển thị kết quả
        request.getRequestDispatcher("/WEB-INF/views/thanks.jsp").forward(request, response);
    }
}
