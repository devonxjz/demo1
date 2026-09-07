<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Hóa đơn thanh toán - Devonxjz</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <div class="container">
        <!-- Header Bar -->
        <jsp:include page="/header.jsp" />

        <c:choose>
            <c:when test="${not empty bill}">
                <div class="alert-success">
                    <strong>Thanh toán thành công! (Mock Payment)</strong>
                    <p>Đơn hàng và hóa đơn của bạn đã được ghi nhận vào cơ sở dữ liệu PostgreSQL (Supabase).</p>
                </div>

                <h1>Hóa đơn thanh toán (Bill)</h1>

                <!-- Thông tin tóm tắt Hóa đơn & Đơn hàng -->
                <div class="bill-info-card">
                    <table class="result-table">
                        <tr>
                            <td class="label-col">Mã hóa đơn (Bill Number):</td>
                            <td><strong style="color: #007b85;">${bill.billNumber}</strong></td>
                        </tr>
                        <tr>
                            <td class="label-col">Mã đơn hàng (Order Number):</td>
                            <td><strong>${bill.order.orderNumber}</strong></td>
                        </tr>
                        <tr>
                            <td class="label-col">Định danh khách hàng:</td>
                            <td><code>${bill.order.customerIdentifier}</code></td>
                        </tr>
                        <tr>
                            <td class="label-col">Thời gian thanh toán:</td>
                            <td>${bill.formattedBillDate}</td>
                        </tr>
                        <tr>
                            <td class="label-col">Phương thức thanh toán:</td>
                            <td>${bill.paymentMethod}</td>
                        </tr>
                        <tr>
                            <td class="label-col">Trạng thái hóa đơn:</td>
                            <td><span style="color: #2e7d32; font-weight: bold;">${bill.paymentStatus}</span></td>
                        </tr>
                    </table>
                </div>

                <h2 class="section-heading">Chi tiết các món hàng đã đặt (Order Details):</h2>
                <table class="cart-table">
                    <thead>
                        <tr>
                            <th>Món hàng</th>
                            <th>Mã sản phẩm</th>
                            <th>Đơn giá</th>
                            <th style="text-align: center;">Số lượng</th>
                            <th>Thành tiền</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="detail" items="${bill.order.orderDetails}">
                            <tr>
                                <td><strong>${detail.productName}</strong></td>
                                <td><code>${detail.productCode}</code></td>
                                <td>${detail.formattedUnitPrice}</td>
                                <td style="text-align: center;">${detail.quantity}</td>
                                <td><strong>${detail.formattedLineTotal}</strong></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                    <tfoot>
                        <tr class="cart-summary-row">
                            <td colspan="4" class="summary-label"><strong>Tổng tiền đã thanh toán:</strong></td>
                            <td class="summary-total"><strong>${bill.formattedTotalAmount}</strong></td>
                        </tr>
                    </tfoot>
                </table>

                <div class="cart-actions" style="margin-top: 24px;">
                    <a href="${pageContext.request.contextPath}/index.jsp" class="btn-primary">Về trang chủ (Home)</a>
                    <a href="${pageContext.request.contextPath}/order" class="btn-link">Tiếp tục mua hàng</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-cart-message">
                    <p>Không tìm thấy thông tin hóa đơn được yêu cầu.</p>
                    <a href="${pageContext.request.contextPath}/index.jsp" class="btn-primary">Về trang chủ</a>
                </div>
            </c:otherwise>
        </c:choose>

        <jsp:include page="/footer.jsp" />
    </div>
</body>
</html>
