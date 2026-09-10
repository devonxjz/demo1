<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký tài khoản - Devonxjz Demo1</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css">
</head>
<body>
    <div class="container auth-container">
        <jsp:include page="/header.jsp" />

        <div class="auth-card">
            <h1 class="auth-title">Tạo Tài Khoản Mới</h1>
            <p class="auth-subtitle">Mật khẩu được mã hóa an toàn với chuẩn BCrypt</p>

            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/register" method="post">
                <div class="form-row-2col">
                    <div class="form-field">
                        <label for="firstName">Tên (First Name):</label>
                        <input type="text" id="firstName" name="firstName" value="${firstName}"
                               placeholder="ví dụ: Nam">
                    </div>
                    <div class="form-field">
                        <label for="lastName">Họ (Last Name):</label>
                        <input type="text" id="lastName" name="lastName" value="${lastName}"
                               placeholder="ví dụ: Nguyễn">
                    </div>
                </div>

                <div class="form-field">
                    <label for="username">Tên đăng nhập (Username) *:</label>
                    <input type="text" id="username" name="username" value="${username}"
                           placeholder="Ít nhất 3 ký tự" required autofocus>
                </div>

                <div class="form-field">
                    <label for="email">Địa chỉ Email *:</label>
                    <input type="email" id="email" name="email" value="${email}"
                           placeholder="ví dụ: user@example.com" required>
                </div>

                <div class="form-field">
                    <label for="password">Mật khẩu *:</label>
                    <input type="password" id="password" name="password"
                           placeholder="Tối thiểu 6 ký tự" required>
                </div>

                <div class="form-field">
                    <label for="confirmPassword">Xác nhận mật khẩu *:</label>
                    <input type="password" id="confirmPassword" name="confirmPassword"
                           placeholder="Nhập lại mật khẩu" required>
                </div>

                <button type="submit" class="btn-auth-submit">Đăng Ký Tài Khoản</button>
            </form>

            <div class="auth-footer-links">
                Đã có tài khoản? <a href="${pageContext.request.contextPath}/login">Đăng nhập ngay tại đây</a>
            </div>
        </div>

        <jsp:include page="/footer.jsp" />
    </div>
</body>
</html>
