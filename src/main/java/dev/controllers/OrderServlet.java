package dev.controllers;

import dev.models.Bill;
import dev.models.Cart;
import dev.models.LineItem;
import dev.models.Product;
import dev.models.User;
import dev.services.OrderService;
import dev.services.ProductService;
import dev.utils.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/order")
public class OrderServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final OrderService orderService = new OrderService();

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
        String userCookie = CookieUtil.getCookieValue(request.getCookies(), CookieUtil.USER_COOKIE_NAME);
        if (user != null) {
            userCookie = user.getEmail();
        } else if (userCookie.isEmpty()) {
            userCookie = "user_" + UUID.randomUUID().toString().substring(0, 8);
            CookieUtil.addCookie(response, CookieUtil.USER_COOKIE_NAME, userCookie, CookieUtil.DEFAULT_COOKIE_MAX_AGE);
        }

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "shop";
        }

        if ("cart".equalsIgnoreCase(action) || "checkout".equalsIgnoreCase(action)) {
            String rawCartCookie = CookieUtil.getCookieValue(request.getCookies(), CookieUtil.CART_COOKIE_NAME);
            String cartObjCookie = CookieUtil.getCookieValue(request.getCookies(), CookieUtil.CART_OBJECT_COOKIE_NAME);
            String userObjCookie = CookieUtil.getCookieValue(request.getCookies(), CookieUtil.USER_OBJECT_COOKIE_NAME);

            request.setAttribute("rawCartCookie", rawCartCookie);
            request.setAttribute("cartObjCookie", cartObjCookie);
            request.setAttribute("userObjCookie", userObjCookie);
            request.setAttribute("userCookieVal", userCookie);
            request.setAttribute("syncedUser", user);
            request.setAttribute("cart", cart);
            request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
        } else if ("bill".equalsIgnoreCase(action)) {
            Bill bill = (Bill) request.getSession().getAttribute("lastBill");
            // Xóa lastBill khỏi session: chỉ hiển thị hóa đơn 1 lần sau khi thanh toán, nếu refresh (F5) sẽ về trang chủ
            request.getSession().removeAttribute("lastBill");
            if (bill == null) {
                response.sendRedirect(request.getContextPath() + "/index.jsp");
                return;
            }
            request.setAttribute("bill", bill);
            request.getRequestDispatcher("/WEB-INF/views/bill.jsp").forward(request, response);
        } else {
            request.setAttribute("products", productService.getAllProducts());
            request.setAttribute("cart", cart);
            request.setAttribute("syncedUser", user);
            request.getRequestDispatcher("/order.jsp").forward(request, response);
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
                String quantityStr = request.getParameter("quantity");
                int quantity = 1;
                if (quantityStr != null && !quantityStr.trim().isEmpty()) {
                    try {
                        quantity = Integer.parseInt(quantityStr.trim());
                    } catch (NumberFormatException ignored) {}
                }
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
                String quantityStr = request.getParameter("quantity");
                int quantity = 0;
                if (quantityStr != null && !quantityStr.trim().isEmpty()) {
                    try {
                        quantity = Integer.parseInt(quantityStr.trim());
                    } catch (NumberFormatException ignored) {}
                }
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
                String customerIdentifier = (user != null && !user.getEmail().isEmpty())
                        ? (user.getFirstName() + " " + user.getLastName() + " <" + user.getEmail() + ">")
                        : CookieUtil.getCookieValue(request.getCookies(), CookieUtil.USER_COOKIE_NAME);

                Bill bill = orderService.createOrderAndBill(cart, customerIdentifier, "Mock Payment");
                cart.clear();
                CookieUtil.syncCart(request, response, cart);

                if (bill != null) {
                    request.getSession().setAttribute("lastBill", bill);
                    response.sendRedirect(request.getContextPath() + "/order?action=bill&billNumber=" + bill.getBillNumber());
                } else {
                    response.sendRedirect(request.getContextPath() + "/order?action=cart&paid=true");
                }
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
}
