<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header class="site-header">
    <div class="header-inner">
        <a class="header-logo" href="${pageContext.request.contextPath}/index.jsp">
            <img src="${pageContext.request.contextPath}/assets/images/logo.jpg" alt="Logo" width="32" height="32">
            <span>Devonxjz</span>
        </a>
        <nav class="header-nav">
            <a href="${pageContext.request.contextPath}/index.jsp" class="nav-link">Survey</a>
            <a href="${pageContext.request.contextPath}/order" class="nav-link">Order / Shop</a>
            <a href="${pageContext.request.contextPath}/index.jsp" class="nav-link refresh-link" title="Làm mới - Trở về trang chủ">
                <svg viewBox="0 0 24 24" width="13" height="13" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" style="vertical-align: -1px; margin-right: 3px;">
                    <path d="M23 4v6h-6"></path>
                    <path d="M1 20v-6h6"></path>
                    <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"></path>
                </svg>
                <span>Làm mới</span>
            </a>
        </nav>
        <div class="header-right" style="display: flex; align-items: center; gap: 12px;">
            <c:if test="${not empty sessionScope.user}">
                <span class="user-greeting" style="font-size: 12.5px; color: #007b85; font-weight: bold;">
                    Xin chào, ${sessionScope.user.firstName}!
                </span>
            </c:if>
            <div class="header-cart">
                <a href="${pageContext.request.contextPath}/order?action=cart" class="cart-btn" title="Xem giỏ hàng">
                    <svg class="cart-icon" viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="9" cy="21" r="1"></circle>
                        <circle cx="20" cy="21" r="1"></circle>
                        <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
                    </svg>
                    <span class="cart-text">Giỏ hàng</span>
                    <span class="cart-badge">${sessionScope.cart != null ? sessionScope.cart.count : 0}</span>
                </a>
            </div>
        </div>
    </div>
</header>
