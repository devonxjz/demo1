package dev.services;

import dev.configurations.EmailConfig;
import dev.models.Bill;
import dev.models.Order;
import dev.models.OrderDetail;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.util.concurrent.CompletableFuture;

/**
 * Service gửi email thông báo đặt hàng và hóa đơn
 */
public class EmailService {

    private final EmailConfig emailConfig;

    public EmailService() {
        this(EmailConfig.getInstance());
    }

    public EmailService(EmailConfig emailConfig) {
        this.emailConfig = emailConfig != null ? emailConfig : EmailConfig.getInstance();
    }

    /**
     * Gửi email xác nhận đặt hàng bất đồng bộ
     */
    public CompletableFuture<Boolean> sendOrderConfirmationAsync(String toEmail, String customerName, Order order, Bill bill) {
        return CompletableFuture.supplyAsync(() -> sendOrderConfirmation(toEmail, customerName, order, bill));
    }

    /**
     * Gửi email xác nhận đặt hàng đồng bộ
     */
    public boolean sendOrderConfirmation(String toEmail, String customerName, Order order, Bill bill) {
        if (toEmail == null || toEmail.trim().isEmpty()) {
            System.err.println("[EmailService] Không thể gửi email: Địa chỉ người nhận trống.");
            return false;
        }

        String recipientEmail = toEmail.trim();
        String name = (customerName != null && !customerName.trim().isEmpty()) ? customerName.trim() : "Quý khách";
        String subject = "Thank for orders - " + (order != null ? order.getOrderNumber() : "");

        if (!emailConfig.isReadyToSend()) {
            System.out.println("[EmailService] SMTP chưa được cấu hình hoặc mail.enabled=false.");
            System.out.println("[EmailService] [MOCK SEND] Gửi email thành công tới: " + recipientEmail);
            System.out.println("[EmailService] [MOCK SEND] Tiêu đề: " + subject);
            System.out.println("[EmailService] [MOCK SEND] Nội dung tóm tắt: Thank for orders. Mã đơn: "
                    + (order != null ? order.getOrderNumber() : "N/A"));
            return true;
        }

        try {
            Session session = createMailSession();
            MimeMessage message = new MimeMessage(session);

            String fromAddress = emailConfig.getFrom();
            message.setFrom(new InternetAddress(fromAddress, "Devonxjz Store", "UTF-8"));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject(subject, "UTF-8");

            MimeMultipart multipart = new MimeMultipart("alternative");

            // Text part
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(buildTextBody(name, order, bill), "UTF-8", "plain");
            multipart.addBodyPart(textPart);

            // HTML part
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(buildHtmlBody(name, order, bill), "text/html; charset=UTF-8");
            multipart.addBodyPart(htmlPart);

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("[EmailService] Đã gửi email xác nhận đặt hàng thành công tới: " + recipientEmail);
            return true;
        } catch (Exception e) {
            System.err.println("[EmailService] Lỗi khi gửi email tới " + recipientEmail + ": " + e.getMessage());
            return false;
        }
    }

    private Session createMailSession() {
        if (emailConfig.isAuth()) {
            return Session.getInstance(emailConfig.toSessionProperties(), new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailConfig.getUsername(), emailConfig.getPassword());
                }
            });
        }
        return Session.getInstance(emailConfig.toSessionProperties());
    }

    public String buildTextBody(String customerName, Order order, Bill bill) {
        StringBuilder sb = new StringBuilder();
        sb.append("Xin chào ").append(customerName).append(",\n\n");
        sb.append("Thank for orders!\n\n");
        sb.append("Cảm ơn bạn đã tin tưởng và đặt hàng tại Devonxjz Store.\n");

        if (order != null) {
            sb.append("Mã đơn hàng (Order Number): ").append(order.getOrderNumber()).append("\n");
            sb.append("Thời gian: ").append(order.getFormattedOrderDate()).append("\n");
            if (bill != null) {
                sb.append("Mã hóa đơn (Bill Number): ").append(bill.getBillNumber()).append("\n");
                sb.append("Phương thức thanh toán: ").append(bill.getPaymentMethod()).append("\n");
                sb.append("Trạng thái: ").append(bill.getPaymentStatus()).append("\n");
            }
            sb.append("\nChi tiết đơn hàng:\n");
            if (order.getOrderDetails() != null) {
                for (OrderDetail detail : order.getOrderDetails()) {
                    sb.append("- ").append(detail.getProductName())
                            .append(" (Mã: ").append(detail.getProductCode()).append(")")
                            .append(" x ").append(detail.getQuantity())
                            .append(" = ").append(detail.getFormattedLineTotal()).append("\n");
                }
            }
            sb.append("\nTổng thanh toán: ").append(order.getFormattedTotalAmount()).append("\n\n");
        }

        sb.append("Trân trọng,\nĐội ngũ Devonxjz Store");
        return sb.toString();
    }

    public String buildHtmlBody(String customerName, Order order, Bill bill) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html><head><meta charset=\"UTF-8\">");
        sb.append("<style>");
        sb.append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f7f9fa; color: #222; margin: 0; padding: 20px; }");
        sb.append(".email-container { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 8px; border: 1px solid #e1e4e8; overflow: hidden; }");
        sb.append(".header { background: #007b85; color: #ffffff; padding: 20px 24px; text-align: center; }");
        sb.append(".header h1 { margin: 0; font-size: 22px; font-weight: 600; }");
        sb.append(".body { padding: 24px; }");
        sb.append(".hero-title { font-size: 18px; font-weight: bold; color: #007b85; margin-bottom: 8px; }");
        sb.append(".meta-box { background: #f0fbfc; border: 1px solid #c9eff2; border-radius: 6px; padding: 14px; margin: 16px 0; font-size: 14px; line-height: 1.6; }");
        sb.append(".table { width: 100%; border-collapse: collapse; margin-top: 16px; font-size: 14px; }");
        sb.append(".table th { background: #f1f3f5; padding: 10px 12px; text-align: left; border-bottom: 2px solid #dee2e6; }");
        sb.append(".table td { padding: 10px 12px; border-bottom: 1px solid #eee; }");
        sb.append(".table tfoot td { font-weight: bold; padding: 12px; border-top: 2px solid #007b85; }");
        sb.append(".footer { background: #fafbfc; border-top: 1px solid #e1e4e8; padding: 16px 24px; text-align: center; font-size: 12px; color: #6a737d; }");
        sb.append("</style></head><body>");

        sb.append("<div class=\"email-container\">");
        sb.append("<div class=\"header\"><h1>Devonxjz Store</h1></div>");
        sb.append("<div class=\"body\">");
        sb.append("<div class=\"hero-title\">Thank for orders!</div>");
        sb.append("<p>Xin chào <strong>").append(customerName).append("</strong>,</p>");
        sb.append("<p>Cảm ơn bạn đã tin tưởng mua sắm tại Devonxjz Store. Đơn hàng của bạn đã được ghi nhận thành công.</p>");

        if (order != null) {
            sb.append("<div class=\"meta-box\">");
            sb.append("<div><strong>Mã đơn hàng:</strong> ").append(order.getOrderNumber()).append("</div>");
            sb.append("<div><strong>Thời gian:</strong> ").append(order.getFormattedOrderDate()).append("</div>");
            if (bill != null) {
                sb.append("<div><strong>Mã hóa đơn:</strong> ").append(bill.getBillNumber()).append("</div>");
                sb.append("<div><strong>Phương thức thanh toán:</strong> ").append(bill.getPaymentMethod()).append("</div>");
                sb.append("<div><strong>Trạng thái:</strong> <span style=\"color:#2e7d32;font-weight:bold;\">")
                        .append(bill.getPaymentStatus()).append("</span></div>");
            }
            sb.append("</div>");

            sb.append("<h3 style=\"margin-top:20px;margin-bottom:8px;font-size:15px;\">Chi tiết đơn hàng</h3>");
            sb.append("<table class=\"table\">");
            sb.append("<thead><tr><th>Sản phẩm</th><th style=\"text-align:center;\">SL</th><th style=\"text-align:right;\">Đơn giá</th><th style=\"text-align:right;\">Thành tiền</th></tr></thead>");
            sb.append("<tbody>");

            if (order.getOrderDetails() != null) {
                for (OrderDetail detail : order.getOrderDetails()) {
                    sb.append("<tr>");
                    sb.append("<td><strong>").append(detail.getProductName()).append("</strong><br><small style=\"color:#6a737d;\">Mã: ")
                            .append(detail.getProductCode()).append("</small></td>");
                    sb.append("<td style=\"text-align:center;\">").append(detail.getQuantity()).append("</td>");
                    sb.append("<td style=\"text-align:right;\">").append(detail.getFormattedUnitPrice()).append("</td>");
                    sb.append("<td style=\"text-align:right;\"><strong>").append(detail.getFormattedLineTotal()).append("</strong></td>");
                    sb.append("</tr>");
                }
            }
            sb.append("</tbody>");
            sb.append("<tfoot><tr><td colspan=\"3\" style=\"text-align:right;\">Tổng cộng:</td><td style=\"text-align:right;color:#007b85;font-size:16px;\">")
                    .append(order.getFormattedTotalAmount()).append("</td></tr></tfoot>");
            sb.append("</table>");
        }

        sb.append("<p style=\"margin-top:24px;font-size:13px;color:#555;\">Nếu có bất kỳ thắc mắc nào, vui lòng liên hệ bộ phận hỗ trợ của chúng tôi.</p>");
        sb.append("</div>");
        sb.append("<div class=\"footer\">&copy; 2026 Devonxjz Store. All rights reserved.</div>");
        sb.append("</div></body></html>");

        return sb.toString();
    }
}
