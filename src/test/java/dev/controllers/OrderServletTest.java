package dev.controllers;

import dev.models.Bill;
import dev.models.Cart;
import dev.models.LineItem;
import dev.models.Order;
import dev.models.Product;
import dev.models.User;
import dev.services.EmailService;
import dev.services.OrderService;
import dev.services.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class OrderServletTest {

    private OrderServlet orderServlet;
    private AtomicBoolean emailSent;
    private AtomicReference<String> sentToEmail;
    private AtomicReference<String> capturedRedirect;
    private Map<String, Object> sessionAttributes;
    private Map<String, String> requestParams;

    @BeforeEach
    void setUp() {
        emailSent = new AtomicBoolean(false);
        sentToEmail = new AtomicReference<>();
        capturedRedirect = new AtomicReference<>();
        sessionAttributes = new HashMap<>();
        requestParams = new HashMap<>();

        OrderService dummyOrderService = new OrderService() {
            @Override
            public Bill createOrderAndBill(Cart cart, String customerIdentifier, String paymentMethod) {
                Order order = Order.builder()
                        .orderNumber("ORD-TEST-123")
                        .customerIdentifier(customerIdentifier)
                        .orderDate(LocalDateTime.now())
                        .totalAmount(99.0)
                        .status("COMPLETED")
                        .build();

                Bill bill = Bill.builder()
                        .billNumber("INV-TEST-456")
                        .order(order)
                        .billDate(LocalDateTime.now())
                        .totalAmount(99.0)
                        .paymentMethod(paymentMethod)
                        .paymentStatus("PAID")
                        .build();
                order.setBill(bill);
                return bill;
            }
        };

        EmailService spyEmailService = new EmailService() {
            @Override
            public CompletableFuture<Boolean> sendOrderConfirmationAsync(String toEmail, String customerName, Order order, Bill bill) {
                emailSent.set(true);
                sentToEmail.set(toEmail);
                return CompletableFuture.completedFuture(true);
            }

            @Override
            public boolean sendOrderConfirmation(String toEmail, String customerName, Order order, Bill bill) {
                emailSent.set(true);
                sentToEmail.set(toEmail);
                return true;
            }
        };

        ProductService dummyProductService = new ProductService();
        orderServlet = new OrderServlet(dummyProductService, dummyOrderService, spyEmailService);
    }

    private HttpServletRequest createMockRequest() {
        return (HttpServletRequest) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("getParameter".equals(name)) {
                        return requestParams.get((String) args[0]);
                    }
                    if ("getSession".equals(name)) {
                        return createMockSession();
                    }
                    if ("getCookies".equals(name)) {
                        return null;
                    }
                    if ("getContextPath".equals(name)) {
                        return "/demo1";
                    }
                    return null;
                }
        );
    }

    private HttpServletResponse createMockResponse() {
        return (HttpServletResponse) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[]{HttpServletResponse.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("sendRedirect".equals(name)) {
                        capturedRedirect.set((String) args[0]);
                        return null;
                    }
                    if ("addCookie".equals(name)) {
                        return null;
                    }
                    return null;
                }
        );
    }

    private HttpSession createMockSession() {
        return (HttpSession) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[]{HttpSession.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("getAttribute".equals(name)) {
                        return sessionAttributes.get((String) args[0]);
                    }
                    if ("setAttribute".equals(name)) {
                        sessionAttributes.put((String) args[0], args[1]);
                        return null;
                    }
                    if ("removeAttribute".equals(name)) {
                        sessionAttributes.remove((String) args[0]);
                        return null;
                    }
                    return null;
                }
        );
    }

    @Test
    void testMockPaymentWithGuestEmailSendsThankYouEmail() throws Exception {
        Cart cart = new Cart();
        cart.addItem(new LineItem(new Product("P1", "Product 1", 50.0), 2));
        sessionAttributes.put("cart", cart);

        requestParams.put("action", "mockPayment");
        requestParams.put("email", "guest@example.com");

        HttpServletRequest request = createMockRequest();
        HttpServletResponse response = createMockResponse();

        // Invoke doPost via reflection to test protected method
        var doPost = OrderServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(orderServlet, request, response);

        assertTrue(emailSent.get(), "Email should be dispatched");
        assertEquals("guest@example.com", sentToEmail.get());
        assertEquals("guest@example.com", sessionAttributes.get("orderEmailSentTo"));
        assertTrue(capturedRedirect.get().contains("billNumber=INV-TEST-456"));
    }

    @Test
    void testMockPaymentWithLoggedInUserSendsThankYouEmail() throws Exception {
        Cart cart = new Cart();
        cart.addItem(new LineItem(new Product("P2", "Product 2", 99.0), 1));
        sessionAttributes.put("cart", cart);

        User user = new User("alice", "alice@example.com", "pass", "Alice", "Wonderland");
        sessionAttributes.put("user", user);

        requestParams.put("action", "mockPayment");

        HttpServletRequest request = createMockRequest();
        HttpServletResponse response = createMockResponse();

        var doPost = OrderServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(orderServlet, request, response);

        assertTrue(emailSent.get(), "Email should be dispatched to logged-in user");
        assertEquals("alice@example.com", sentToEmail.get());
        assertEquals("alice@example.com", sessionAttributes.get("orderEmailSentTo"));
        assertTrue(capturedRedirect.get().contains("billNumber=INV-TEST-456"));
    }
}
