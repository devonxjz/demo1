<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Giỏ hàng & Thanh toán - Devonxjz</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <div class="container order-container">
        <!-- Header Bar -->
        <jsp:include page="/header.jsp" />

        <h1>Giỏ hàng của bạn</h1>

        <!-- Thông báo Mock Payment thành công nếu có -->
        <c:if test="${param.paid == 'true'}">
            <div class="alert-success">
                <strong>Thanh toán thành công! (Mock Payment)</strong>
                <p>Cảm ơn bạn đã mua hàng. Giỏ hàng và Cookie giỏ hàng của bạn đã được làm mới.</p>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${empty cart.items}">
                <div class="empty-cart-message">
                    <p>Giỏ hàng của bạn hiện đang trống.</p>
                    <a href="${pageContext.request.contextPath}/order" class="btn-primary">Mua sắm ngay</a>
                </div>
            </c:when>
            <c:otherwise>
                <p class="intro">Xem lại các món hàng đã chọn. Bạn có thể <strong>chỉnh sửa số lượng</strong> hoặc xóa món hàng trước khi thanh toán.</p>

                <!-- Bảng danh sách món hàng trong giỏ -->
                <table class="cart-table">
                    <thead>
                        <tr>
                            <th class="col-item">Món hàng</th>
                            <th class="col-price">Đơn giá</th>
                            <th class="col-qty">Số lượng</th>
                            <th class="col-total">Thành tiền</th>
                            <th class="col-remove">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${cart.items}">
                            <tr>
                                <td class="col-item">
                                    <strong>${item.product.name}</strong>
                                    <div class="product-code">Mã: ${item.product.code}</div>
                                </td>
                                <td class="col-price">${item.product.formattedPrice}</td>
                                <td class="col-qty">
                                    <!-- Form chỉnh sửa số lượng từng món -->
                                    <form action="${pageContext.request.contextPath}/order" method="post" class="qty-form">
                                        <input type="hidden" name="action" value="update">
                                        <input type="hidden" name="productCode" value="${item.product.code}">
                                        <input type="number" name="quantity" value="${item.quantity}" min="1" max="99" class="qty-input">
                                        <button type="submit" class="btn-sm" title="Cập nhật số lượng">Cập nhật</button>
                                    </form>
                                </td>
                                <td class="col-total"><strong>${item.formattedTotal}</strong></td>
                                <td class="col-remove">
                                    <!-- Form xóa món hàng -->
                                    <form action="${pageContext.request.contextPath}/order" method="post" onsubmit="return confirm('Bạn có chắc muốn xóa món hàng này khỏi giỏ?');">
                                        <input type="hidden" name="action" value="remove">
                                        <input type="hidden" name="productCode" value="${item.product.code}">
                                        <button type="submit" class="btn-remove">Xóa</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                    <tfoot>
                        <tr class="cart-summary-row">
                            <td colspan="3" class="summary-label"><strong>Tổng cộng tiền hàng:</strong></td>
                            <td colspan="2" class="summary-total"><strong>${cart.formattedTotalAmount}</strong></td>
                        </tr>
                    </tfoot>
                </table>

                <!-- Thông tin người nhận email xác nhận đơn hàng -->
                <div class="checkout-email-card" style="margin: 20px 0; padding: 16px 20px; background: #ffffff; border: 1px solid #d0e7e9; border-left: 4px solid #007b85; border-radius: 6px; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
                    <c:choose>
                        <c:when test="${not empty syncedUser and not empty syncedUser.email}">
                            <div style="display: flex; align-items: center; gap: 12px;">
                                <div style="font-size: 24px;">📧</div>
                                <div>
                                    <div style="font-weight: 600; color: #007b85; font-size: 15px;">Email nhận thông báo đặt hàng ("Thank for orders")</div>
                                    <div style="font-size: 13.5px; color: #444; margin-top: 3px;">
                                        Đang đăng nhập: <strong>${syncedUser.displayName}</strong> &lt;<strong>${syncedUser.email}</strong>&gt;
                                    </div>
                                    <div style="font-size: 12px; color: #666; margin-top: 2px;">
                                        Hệ thống sẽ tự động gửi email cảm ơn & chi tiết đơn hàng đến hộp thư này ngay sau khi hoàn tất.
                                    </div>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div>
                                <label for="checkoutGuestEmail" style="display: block; font-weight: 600; color: #007b85; font-size: 15px; margin-bottom: 6px;">
                                    📧 Email nhận xác nhận đơn hàng ("Thank for orders")
                                </label>
                                <p style="font-size: 13px; color: #555; margin: 0 0 10px 0;">
                                    Bạn đang đặt hàng với tư cách khách vãng lai. Vui lòng nhập địa chỉ email để nhận thông báo và hóa đơn:
                                </p>
                                <div style="max-width: 420px;">
                                    <input type="email" id="checkoutGuestEmail" name="guestEmailInput"
                                           value="${not empty userCookieEmail ? userCookieEmail : ''}"
                                           placeholder="vi-du@domain.com"
                                           class="form-control"
                                           style="width: 100%; box-sizing: border-box; padding: 9px 12px; border: 1px solid #ccc; border-radius: 4px; font-size: 14px;"
                                           oninput="document.getElementById('checkoutFormEmail').value = this.value;">
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Các nút hành động chính -->
                <div class="cart-actions" style="display: flex; flex-wrap: wrap; gap: 12px; align-items: center; justify-content: flex-end;">
                    <a href="${pageContext.request.contextPath}/order" class="btn-link">Tiếp tục mua hàng</a>
                    
                    <form action="${pageContext.request.contextPath}/order" method="post" class="inline-form">
                        <input type="hidden" name="action" value="clear">
                        <button type="submit" class="btn-link btn-secondary" onclick="return confirm('Bạn có chắc muốn làm trống giỏ hàng?');">Làm trống giỏ hàng</button>
                    </form>

                    <form action="${pageContext.request.contextPath}/order" method="post" class="inline-form" id="checkoutForm" onsubmit="var g = document.getElementById('checkoutGuestEmail'); if (g) { document.getElementById('checkoutFormEmail').value = g.value.trim(); } return true;">
                        <input type="hidden" name="action" value="mockPayment">
                        <input type="hidden" name="email" id="checkoutFormEmail" value="${not empty syncedUser and not empty syncedUser.email ? syncedUser.email : (not empty userCookieEmail ? userCookieEmail : '')}">
                        <button type="submit" class="btn-secondary btn-checkout" style="background: #6c757d; color: #fff; padding: 10px 18px; border-radius: 4px; border: none; cursor: pointer;">Thanh toán thử nghiệm (Mock)</button>
                    </form>

                    <form action="${pageContext.request.contextPath}/order" method="post" class="inline-form" id="vnpayCheckoutForm" onsubmit="var g = document.getElementById('checkoutGuestEmail'); if (g) { document.getElementById('vnpayFormEmail').value = g.value.trim(); } return true;">
                        <input type="hidden" name="action" value="vnpayPayment">
                        <input type="hidden" name="email" id="vnpayFormEmail" value="${not empty syncedUser and not empty syncedUser.email ? syncedUser.email : (not empty userCookieEmail ? userCookieEmail : '')}">
                        <button type="submit" class="btn-primary btn-checkout" style="background: #005baa; color: #fff; font-weight: 600; padding: 10px 20px; border-radius: 4px; border: none; cursor: pointer; box-shadow: 0 2px 6px rgba(0,91,170,0.3);">
                            💳 Thanh toán qua VNPAY
                        </button>
                    </form>
                </div>

            </c:otherwise>
        </c:choose>

        <jsp:include page="/footer.jsp" />
    </div>
</body>
</html>
