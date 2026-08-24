package dev;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/survey")
public class SurveyServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Lấy dữ liệu từ form
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
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

        // 3. Đưa đối tượng user vào request attribute
        request.setAttribute("user", user);

        // 4. Chuyển tiếp (forward) sang trang JSP hiển thị kết quả
        request.getRequestDispatcher("/thanks.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("index.html");
    }
}
