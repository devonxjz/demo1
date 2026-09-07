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
    <div class="container">
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

                <!-- Các nút hành động chính -->
                <div class="cart-actions">
                    <a href="${pageContext.request.contextPath}/order" class="btn-link">Tiếp tục mua hàng</a>
                    
                    <form action="${pageContext.request.contextPath}/order" method="post" class="inline-form">
                        <input type="hidden" name="action" value="clear">
                        <button type="submit" class="btn-link btn-secondary" onclick="return confirm('Bạn có chắc muốn làm trống giỏ hàng?');">Làm trống giỏ hàng</button>
                    </form>

                    <form action="${pageContext.request.contextPath}/order" method="post" class="inline-form">
                        <input type="hidden" name="action" value="mockPayment">
                        <button type="submit" class="btn-primary btn-checkout">Thanh toán (Mock Payment)</button>
                    </form>
                </div>
            </c:otherwise>
        </c:choose>

        <jsp:include page="/footer.jsp" />
    </div>
</body>
</html>
