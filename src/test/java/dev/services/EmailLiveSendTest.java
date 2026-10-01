package dev.services;

import dev.configurations.EmailConfig;
import dev.models.Bill;
import dev.models.Order;
import dev.models.OrderDetail;
import dev.models.Product;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailLiveSendTest {

    @Test
    @Disabled("Chỉ chạy thủ công khi cần kiểm tra gửi mail thực tế qua SMTP")
    void testLiveSend() {
        EmailConfig config = EmailConfig.getInstance();
        System.out.println("[LiveTest] Ready to send: " + config.isReadyToSend());
        System.out.println("[LiveTest] Host: " + config.getHost() + ":" + config.getPort());
        System.out.println("[LiveTest] From: " + config.getFrom());

        EmailService service = new EmailService(config);

        Order order = Order.builder()
                .id(999L)
                .orderNumber("ORD-LIVE-TEST")
                .orderDate(LocalDateTime.now())
                .totalAmount(49.99)
                .build();

        Product product = new Product("TEST01", "Kiểm tra hệ thống gửi email", 49.99);
        OrderDetail detail = OrderDetail.builder()
                .product(product)
                .productCode("TEST01")
                .productName("Kiểm tra hệ thống gửi email")
                .quantity(1)
                .unitPrice(49.99)
                .lineTotal(49.99)
                .build();
        order.setOrderDetails(List.of(detail));

        Bill bill = Bill.builder()
                .billNumber("INV-LIVE-TEST")
                .order(order)
                .billDate(LocalDateTime.now())
                .totalAmount(49.99)
                .paymentMethod("Mock Payment")
                .paymentStatus("PAID")
                .build();

        boolean sent = service.sendOrderConfirmation(config.getUsername(), "Lê Thái", order, bill);
        System.out.println("[LiveTest] Sent outcome: " + sent);
        assertTrue(sent, "Email phải gửi thành công");
    }
}
