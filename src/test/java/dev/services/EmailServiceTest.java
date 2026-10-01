package dev.services;

import dev.configurations.EmailConfig;
import dev.models.Bill;
import dev.models.Order;
import dev.models.OrderDetail;
import dev.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class EmailServiceTest {

    private EmailService emailService;
    private Order sampleOrder;
    private Bill sampleBill;

    @BeforeEach
    void setUp() {
        Properties props = new Properties();
        props.setProperty("mail.enabled", "false");
        props.setProperty("mail.smtp.username", "");
        props.setProperty("mail.smtp.password", "");
        props.setProperty("mail.from", "noreply@demo1.dev");

        EmailConfig config = new EmailConfig(props);
        emailService = new EmailService(config);

        sampleOrder = Order.builder()
                .id(1L)
                .orderNumber("ORD-123456")
                .customerIdentifier("John Doe <john@example.com>")
                .orderDate(LocalDateTime.now())
                .totalAmount(49.99)
                .status("COMPLETED")
                .orderDetails(new ArrayList<>())
                .build();

        Product prod = new Product("PROD01", "86 (That's what I want)", 49.99);
        OrderDetail detail = OrderDetail.builder()
                .id(1L)
                .order(sampleOrder)
                .product(prod)
                .productCode("PROD01")
                .productName("86 (That's what I want)")
                .unitPrice(49.99)
                .quantity(1)
                .lineTotal(49.99)
                .build();
        sampleOrder.setOrderDetails(List.of(detail));

        sampleBill = Bill.builder()
                .id(1L)
                .billNumber("INV-987654")
                .order(sampleOrder)
                .billDate(LocalDateTime.now())
                .totalAmount(49.99)
                .paymentMethod("Mock Payment")
                .paymentStatus("PAID")
                .build();
        sampleOrder.setBill(sampleBill);
    }

    @Test
    void testSendEmailWithNullOrEmptyRecipient() {
        assertFalse(emailService.sendOrderConfirmation(null, "John", sampleOrder, sampleBill));
        assertFalse(emailService.sendOrderConfirmation("", "John", sampleOrder, sampleBill));
        assertFalse(emailService.sendOrderConfirmation("   ", "John", sampleOrder, sampleBill));
    }

    @Test
    void testEmailContentGeneration() {
        String content = emailService.buildTextBody("John Doe", sampleOrder, sampleBill);
        assertNotNull(content);
        assertTrue(content.contains("Thank for orders"));
        assertTrue(content.contains("ORD-123456"));
        assertTrue(content.contains("INV-987654"));
        assertTrue(content.contains("86 (That's what I want)"));
    }

    @Test
    void testHtmlEmailContentGeneration() {
        String html = emailService.buildHtmlBody("John Doe", sampleOrder, sampleBill);
        assertNotNull(html);
        assertTrue(html.contains("Thank for orders"));
        assertTrue(html.contains("ORD-123456"));
        assertTrue(html.contains("INV-987654"));
        assertTrue(html.contains("86 (That's what I want)"));
    }

    @Test
    void testSimulatedSendWhenMailDisabled() {
        // Mail disabled in config -> logs mock and returns true
        boolean result = emailService.sendOrderConfirmation("john@example.com", "John Doe", sampleOrder, sampleBill);
        assertTrue(result);
    }

    @Test
    void testAsyncSendReturnsCompletedFuture() throws Exception {
        CompletableFuture<Boolean> future = emailService.sendOrderConfirmationAsync("john@example.com", "John Doe", sampleOrder, sampleBill);
        assertNotNull(future);
        Boolean result = future.get();
        assertTrue(result);
    }
}
