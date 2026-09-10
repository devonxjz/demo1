<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập - Devonxjz Demo1</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css">
</head>
<body>
    <div class="container auth-container">
        <jsp:include page="/header.jsp" />

        <div class="auth-card">
            <h1 class="auth-title">Đăng Nhập Tài Khoản</h1>
            <p class="auth-subtitle">Nhập tài khoản để tiếp tục mua hàng và tải tài nguyên</p>

            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <c:if test="${param.loggedOut eq 'true'}">
                <div class="alert alert-success">Bạn đã đăng xuất thành công khỏi hệ thống.</div>
            </c:if>

            <c:if test="${param.registered eq 'true'}">
                <div class="alert alert-success">Đăng ký thành công! Vui lòng đăng nhập.</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="post">
                <div class="form-field">
                    <label for="identifier">Tên đăng nhập hoặc Email:</label>
                    <input type="text" id="identifier" name="identifier" value="${identifier}"
                           placeholder="ví dụ: devon hoặc devon@example.com" required autofocus>
                </div>

                <div class="form-field">
                    <label for="password">Mật khẩu:</label>
                    <input type="password" id="password" name="password"
                           placeholder="Nhập mật khẩu" required>
                </div>

                <button type="submit" class="btn-auth-submit">Đăng Nhập</button>
            </form>

            <div class="auth-footer-links">
                Chưa có tài khoản? <a href="${pageContext.request.contextPath}/register">Đăng ký ngay tại đây</a>
            </div>
        </div>

        <jsp:include page="/footer.jsp" />
    </div>
</body>
</html>
