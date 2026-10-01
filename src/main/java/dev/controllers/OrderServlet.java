package dev.controllers;

import dev.models.Bill;
import dev.models.Cart;
import dev.models.LineItem;
import dev.models.Product;
import dev.models.User;
import dev.services.EmailService;
import dev.services.OrderService;
import dev.services.ProductService;
import dev.utils.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/order")
public class OrderServlet extends HttpServlet {

    private final ProductService productService;
    private final OrderService orderService;
    private final EmailService emailService;
    private final dev.services.VnPayService vnPayService;

    public OrderServlet() {
        this(new ProductService(), new OrderService(), new EmailService(), new dev.services.VnPayService());
    }

    public OrderServlet(ProductService productService, OrderService orderService, EmailService emailService) {
        this(productService, orderService, emailService, new dev.services.VnPayService());
    }

    public OrderServlet(ProductService productService, OrderService orderService, EmailService emailService, dev.services.VnPayService vnPayService) {
        this.productService = productService;
        this.orderService = orderService;
        this.emailService = emailService;
        this.vnPayService = vnPayService;
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processGet(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processPost(request, response);
    }

    private void processGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Đồng bộ Cart từ Session hoặc Object Cookie
        Cart cart = CookieUtil.getSyncedCart(request, productService);

        // Đồng bộ User từ Session hoặc Object Cookie
        User user = CookieUtil.getSyncedUser(request);

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "shop";
        }

        if ("cart".equalsIgnoreCase(action) || "checkout".equalsIgnoreCase(action)) {
            String userCookieEmail = CookieUtil.getCookieValue(request.getCookies(), CookieUtil.USER_COOKIE_NAME);
            request.setAttribute("userCookieEmail", userCookieEmail);
            request.setAttribute("syncedUser", user);
            request.setAttribute("cart", cart);
            request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
        } else if ("bill".equalsIgnoreCase(action)) {
            Bill bill = (Bill) request.getSession().getAttribute("lastBill");
            String orderEmailSentTo = (String) request.getSession().getAttribute("orderEmailSentTo");
            // Xóa lastBill khỏi session: chỉ hiển thị hóa đơn 1 lần sau khi thanh toán, nếu refresh (F5) sẽ về trang chủ
            request.getSession().removeAttribute("lastBill");
            request.getSession().removeAttribute("orderEmailSentTo");
            if (bill == null) {
                response.sendRedirect(request.getContextPath() + "/index.jsp");
                return;
            }
            request.setAttribute("bill", bill);
            request.setAttribute("orderEmailSentTo", orderEmailSentTo);
            request.getRequestDispatcher("/WEB-INF/views/bill.jsp").forward(request, response);
        } else {
            request.setAttribute("products", productService.getAllProducts());
            request.setAttribute("cart", cart);
            request.setAttribute("syncedUser", user);
            request.getRequestDispatcher("/WEB-INF/views/order.jsp").forward(request, response);
        }
    }

    private void processPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Cart cart = CookieUtil.getSyncedCart(request, productService);
        String action = request.getParameter("action");
        if (action == null) {
            action = "shop";
        }

        switch (action.toLowerCase()) {
            case "add": {
                String productCode = request.getParameter("productCode");
                int quantity = parseIntOrDefault(request.getParameter("quantity"), 1);
                Product product = productService.getProductByCode(productCode);
                if (product != null && quantity > 0) {
                    cart.addItem(new LineItem(product, quantity));
                    CookieUtil.syncCart(request, response, cart);
                }
                response.sendRedirect(request.getContextPath() + "/order?action=cart");
                break;
            }
            case "update": {
                String productCode = request.getParameter("productCode");
                int quantity = parseIntOrDefault(request.getParameter("quantity"), 0);
                cart.updateItem(productCode, quantity);
                CookieUtil.syncCart(request, response, cart);
                response.sendRedirect(request.getContextPath() + "/order?action=cart");
                break;
            }
            case "remove": {
                String productCode = request.getParameter("productCode");
                cart.removeItem(productCode);
                CookieUtil.syncCart(request, response, cart);
                response.sendRedirect(request.getContextPath() + "/order?action=cart");
                break;
            }
            case "mockpayment": {
                User user = CookieUtil.getSyncedUser(request);
                String emailParam = request.getParameter("email");
                String cookieEmail = CookieUtil.getCookieValue(request.getCookies(), CookieUtil.USER_COOKIE_NAME);

                String recipientEmail = "";
                String customerName = "Quý khách";
                String customerIdentifier;

                if (user != null && user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
                    recipientEmail = user.getEmail().trim();
                    customerName = user.getDisplayName();
                    customerIdentifier = user.getFirstName() + " " + user.getLastName() + " <" + recipientEmail + ">";
                } else if (emailParam != null && !emailParam.trim().isEmpty()) {
                    recipientEmail = emailParam.trim();
                    customerName = "Guest";
                    customerIdentifier = "Guest <" + recipientEmail + ">";
                    CookieUtil.addCookie(response, CookieUtil.USER_COOKIE_NAME, recipientEmail, CookieUtil.DEFAULT_COOKIE_MAX_AGE);
                } else if (cookieEmail != null && !cookieEmail.trim().isEmpty() && cookieEmail.contains("@")) {
                    recipientEmail = cookieEmail.trim();
                    customerName = "Guest";
                    customerIdentifier = "Guest <" + recipientEmail + ">";
                } else {
                    customerIdentifier = "Guest";
                }

                Bill bill = orderService.createOrderAndBill(cart, customerIdentifier, "Mock Payment");
                cart.clear();
                CookieUtil.syncCart(request, response, cart);

                if (bill != null) {
                    if (!recipientEmail.isEmpty()) {
                        emailService.sendOrderConfirmationAsync(recipientEmail, customerName, bill.getOrder(), bill);
                        request.getSession().setAttribute("orderEmailSentTo", recipientEmail);
                    } else {
                        request.getSession().removeAttribute("orderEmailSentTo");
                    }
                    request.getSession().setAttribute("lastBill", bill);
                    response.sendRedirect(request.getContextPath() + "/order?action=bill&billNumber=" + bill.getBillNumber());
                } else {
                    response.sendRedirect(request.getContextPath() + "/order?action=cart&paid=true");
                }
                break;
            }
            case "vnpaypayment": {
                if (cart == null || cart.getItems().isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/order?action=cart");
                    break;
                }

                String emailParam = request.getParameter("email");
                if (emailParam != null && !emailParam.trim().isEmpty()) {
                    CookieUtil.addCookie(response, CookieUtil.USER_COOKIE_NAME, emailParam.trim(), CookieUtil.DEFAULT_COOKIE_MAX_AGE);
                }

                double total = cart.getTotalAmount();
                long amountVnd = total < 1000 ? Math.round(total * 25000) : Math.round(total);
                String orderNumber = "ORD-" + System.currentTimeMillis() + "-" + (new java.util.Random().nextInt(900) + 100);
                String bankCode = request.getParameter("bankCode");
                String ipAddr = dev.configurations.VnPayConfig.getIpAddress(request);
                String returnUrl = dev.configurations.VnPayConfig.vnp_ReturnUrl;
                String orderInfo = "Thanh toan don hang " + orderNumber;

                String paymentUrl = vnPayService.createPaymentUrl(amountVnd, bankCode, orderInfo, orderNumber, ipAddr, returnUrl, "vn");
                response.sendRedirect(paymentUrl);
                break;
            }
            case "clear": {
                cart.clear();
                CookieUtil.syncCart(request, response, cart);
                response.sendRedirect(request.getContextPath() + "/order?action=cart");
                break;
            }

            default:
                response.sendRedirect(request.getContextPath() + "/order");
                break;
        }
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
