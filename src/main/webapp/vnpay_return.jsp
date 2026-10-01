<%@page import="java.net.URLEncoder"%>
<%@page import="java.nio.charset.StandardCharsets"%>
<%@page import="dev.configurations.VnPayConfig"%>
<%@page import="dev.services.OrderService"%>
<%@page import="dev.services.ProductService"%>
<%@page import="dev.services.EmailService"%>
<%@page import="dev.models.Cart"%>
<%@page import="dev.models.Bill"%>
<%@page import="dev.models.User"%>
<%@page import="dev.utils.CookieUtil"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Kết Quả Thanh Toán VNPAY - Demo1</title>
        <!-- Bootstrap core CSS -->
        <link href="${pageContext.request.contextPath}/assets/vnpay/bootstrap.min.css" rel="stylesheet"/>
        <!-- Custom styles -->
        <link href="${pageContext.request.contextPath}/assets/vnpay/jumbotron-narrow.css" rel="stylesheet"> 
        <script src="${pageContext.request.contextPath}/assets/vnpay/jquery-1.11.3.min.js"></script>
    </head>
    <body>
        <%
            // 1. Thu thập và mã hóa các tham số phản hồi từ VNPAY để kiểm tra chữ ký
            Map<String, String> fields = new HashMap<>();
            for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
                String rawName = params.nextElement();
                String fieldName = URLEncoder.encode(rawName, StandardCharsets.US_ASCII.toString());
                String rawValue = request.getParameter(rawName);
                if (rawValue != null && !rawValue.isEmpty()) {
                    String fieldValue = URLEncoder.encode(rawValue, StandardCharsets.US_ASCII.toString());
                    fields.put(fieldName, fieldValue);
                }
            }

            String vnp_SecureHash = request.getParameter("vnp_SecureHash");
            if (fields.containsKey("vnp_SecureHashType")) {
                fields.remove("vnp_SecureHashType");
            }
            if (fields.containsKey("vnp_SecureHash")) {
                fields.remove("vnp_SecureHash");
            }
            String signValue = VnPayConfig.hashAllFields(fields);

            boolean isSignatureValid = signValue.equalsIgnoreCase(vnp_SecureHash);
            String responseCode = request.getParameter("vnp_ResponseCode");
            String transactionStatus = request.getParameter("vnp_TransactionStatus");
            boolean isSuccess = isSignatureValid && "00".equals(responseCode) && "00".equals(transactionStatus);

            String txnRef = request.getParameter("vnp_TxnRef");
            String amountStr = request.getParameter("vnp_Amount");
            long displayAmount = 0;
            if (amountStr != null) {
                try {
                    displayAmount = Long.parseLong(amountStr) / 100;
                } catch (Exception ignored) {}
            }

            // 2. Nếu thanh toán thành công, tự động chốt đơn hàng và lưu Bill cho Demo1 (nếu có cart)
            Bill createdBill = null;
            if (isSuccess) {
                try {
                    ProductService productService = new ProductService();
                    OrderService orderService = new OrderService();
                    EmailService emailService = new EmailService();

                    Cart cart = CookieUtil.getSyncedCart(request, productService);
                    User user = CookieUtil.getSyncedUser(request);

                    if (cart != null && !cart.getItems().isEmpty()) {
                        String recipientEmail = "";
                        String customerName = "Quý khách";
                        String customerIdentifier;

                        if (user != null && user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
                            recipientEmail = user.getEmail().trim();
                            customerName = user.getDisplayName();
                            customerIdentifier = user.getFirstName() + " " + user.getLastName() + " <" + recipientEmail + ">";
                        } else {
                            String cookieEmail = CookieUtil.getCookieValue(request.getCookies(), CookieUtil.USER_COOKIE_NAME);
                            if (cookieEmail != null && !cookieEmail.trim().isEmpty() && cookieEmail.contains("@")) {
                                recipientEmail = cookieEmail.trim();
                                customerName = "Guest";
                                customerIdentifier = "Guest <" + recipientEmail + ">";
                            } else {
                                customerIdentifier = "Guest";
                            }
                        }

                        createdBill = orderService.createOrderAndBill(cart, customerIdentifier, "VNPAY", txnRef);
                        cart.clear();
                        CookieUtil.syncCart(request, response, cart);

                        if (createdBill != null) {
                            if (!recipientEmail.isEmpty()) {
                                emailService.sendOrderConfirmationAsync(recipientEmail, customerName, createdBill.getOrder(), createdBill);
                                session.setAttribute("orderEmailSentTo", recipientEmail);
                            }
                            session.setAttribute("lastBill", createdBill);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[vnpay_return.jsp] Error processing order completion: " + e.getMessage());
                }
            }
        %>
        <!-- Begin display -->
        <div class="container" style="max-width: 720px; margin-top: 30px;">
            <div class="header clearfix" style="border-bottom: 1px solid #e5e5e5; padding-bottom: 15px; margin-bottom: 25px;">
                <nav>
                    <ul class="nav nav-pills pull-right">
                        <li role="presentation"><a href="${pageContext.request.contextPath}/order">Về Cửa Hàng</a></li>
                        <li role="presentation"><a href="${pageContext.request.contextPath}/vnpay_pay.jsp">Thanh Toán Khác</a></li>
                    </ul>
                </nav>
                <h3 class="text-muted"><a href="${pageContext.request.contextPath}/" style="text-decoration: none; color: #333;">DEMO1 STORE</a></h3>
            </div>

            <div class="panel <%= isSuccess ? "panel-success" : "panel-danger" %>" style="box-shadow: 0 4px 12px rgba(0,0,0,0.08);">
                <div class="panel-heading" style="text-align: center; padding: 25px 20px;">
                    <% if (isSuccess) { %>
                        <div style="font-size: 48px; color: #3c763d; margin-bottom: 10px;">✓</div>
                        <h2 style="margin: 0; font-weight: 700; color: #3c763d;">THANH TOÁN THÀNH CÔNG</h2>
                        <p style="margin-top: 8px; color: #555;">Giao dịch qua cổng VNPAY đã được hoàn tất và ghi nhận.</p>
                    <% } else { %>
                        <div style="font-size: 48px; color: #a94442; margin-bottom: 10px;">✕</div>
                        <h2 style="margin: 0; font-weight: 700; color: #a94442;">THANH TOÁN KHÔNG THÀNH CÔNG</h2>
                        <p style="margin-top: 8px; color: #555;">
                            <%= !isSignatureValid ? "Chữ ký không hợp lệ (Sai checksum)!" : "Giao dịch bị hủy hoặc xảy ra lỗi trong quá trình xử lý." %>
                        </p>
                    <% } %>
                </div>

                <div class="panel-body" style="padding: 25px 30px;">
                    <h4 style="border-bottom: 2px solid #eee; padding-bottom: 10px; margin-bottom: 15px;">Thông tin giao dịch</h4>
                    <table class="table table-striped table-bordered">
                        <tbody>
                            <tr>
                                <th style="width: 40%;">Mã giao dịch (TxnRef):</th>
                                <td><strong><%= request.getParameter("vnp_TxnRef") != null ? request.getParameter("vnp_TxnRef") : "-" %></strong></td>
                            </tr>
                            <tr>
                                <th>Số tiền thanh toán:</th>
                                <td><strong style="color: #007b85; font-size: 16px;"><%= String.format("%,d VNĐ", displayAmount) %></strong></td>
                            </tr>
                            <tr>
                                <th>Nội dung giao dịch:</th>
                                <td><%= request.getParameter("vnp_OrderInfo") != null ? request.getParameter("vnp_OrderInfo") : "-" %></td>
                            </tr>
                            <tr>
                                <th>Mã phản hồi VNPAY:</th>
                                <td><%= responseCode != null ? responseCode : "-" %></td>
                            </tr>
                            <tr>
                                <th>Mã GD tại cổng VNPAY:</th>
                                <td><%= request.getParameter("vnp_TransactionNo") != null ? request.getParameter("vnp_TransactionNo") : "-" %></td>
                            </tr>
                            <tr>
                                <th>Ngân hàng thanh toán:</th>
                                <td><%= request.getParameter("vnp_BankCode") != null ? request.getParameter("vnp_BankCode") : "-" %></td>
                            </tr>
                            <tr>
                                <th>Thời gian thanh toán:</th>
                                <td><%= request.getParameter("vnp_PayDate") != null ? request.getParameter("vnp_PayDate") : "-" %></td>
                            </tr>
                            <tr>
                                <th>Trạng thái chữ ký:</th>
                                <td>
                                    <% if (isSignatureValid) { %>
                                        <span class="label label-success">Hợp lệ (Checksum Passed)</span>
                                    <% } else { %>
                                        <span class="label label-danger">Không hợp lệ (Invalid Signature)</span>
                                    <% } %>
                                </td>
                            </tr>
                        </tbody>
                    </table>

                    <div style="text-align: center; margin-top: 25px;">
                        <% if (createdBill != null) { %>
                            <a href="${pageContext.request.contextPath}/order?action=bill" class="btn btn-success btn-lg">Xem Hóa Đơn Chi Tiết</a>
                        <% } %>
                        <a href="${pageContext.request.contextPath}/order" class="btn btn-primary btn-lg" style="margin-left: 10px;">Tiếp Tục Mua Sắm</a>
                        <a href="${pageContext.request.contextPath}/" class="btn btn-default btn-lg" style="margin-left: 10px;">Trang Chủ</a>
                    </div>
                </div>
            </div>

            <footer class="footer" style="padding-top: 19px; color: #777; border-top: 1px solid #e5e5e5; text-align: center;">
                <p>&copy; VNPAY 2026 - Demo1 Application</p>
            </footer>
        </div>
    </body>
</html>
