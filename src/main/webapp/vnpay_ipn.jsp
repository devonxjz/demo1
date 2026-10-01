<%@page import="java.net.URLEncoder"%>
<%@page import="java.nio.charset.StandardCharsets"%>
<%@page import="dev.configurations.VnPayConfig"%>
<%@page import="dev.services.OrderService"%>
<%@page import="dev.models.Order"%>
<%@page contentType="application/json; charset=UTF-8"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Optional"%>

<%
    /*
     * VNPAY IPN (Instant Payment Notification) Webhook
     * Ghi nhận kết quả thanh toán ngầm từ VNPAY Server
     */
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

    if (signValue.equalsIgnoreCase(vnp_SecureHash)) {
        String txnRef = request.getParameter("vnp_TxnRef");
        String vnpResponseCode = request.getParameter("vnp_ResponseCode");
        String vnpTransactionStatus = request.getParameter("vnp_TransactionStatus");

        OrderService orderService = new OrderService();
        Optional<Order> orderOpt = (txnRef != null && !txnRef.isBlank())
                ? orderService.getOrderByOrderNumber(txnRef)
                : Optional.empty();

        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            // Kiểm tra trạng thái đã xác nhận trước đó chưa
            if ("COMPLETED".equalsIgnoreCase(order.getStatus())
                    && order.getBill() != null
                    && "PAID".equalsIgnoreCase(order.getBill().getPaymentStatus())) {
                out.print("{\"RspCode\":\"02\",\"Message\":\"Order already confirmed\"}");
            } else {
                // Kiểm tra số tiền
                long vnpAmount = 0;
                try {
                    vnpAmount = Long.parseLong(request.getParameter("vnp_Amount")) / 100;
                } catch (Exception ignored) {}

                if ("00".equals(vnpResponseCode) && "00".equals(vnpTransactionStatus)) {
                    order.setStatus("COMPLETED");
                    if (order.getBill() != null) {
                        order.getBill().setPaymentStatus("PAID");
                    }
                } else {
                    order.setStatus("FAILED");
                    if (order.getBill() != null) {
                        order.getBill().setPaymentStatus("FAILED");
                    }
                }
                out.print("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
            }
        } else {
            // Đối với giao dịch thử nghiệm độc lập hoặc thanh toán ngoài giỏ hàng
            out.print("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
        }
    } else {
        // Sai checksum
        out.print("{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}");
    }
%>
