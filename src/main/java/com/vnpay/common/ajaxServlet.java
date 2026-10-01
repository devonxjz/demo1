package com.vnpay.common;

import dev.configurations.VnPayConfig;
import dev.models.Cart;
import dev.services.ProductService;
import dev.services.VnPayService;
import dev.utils.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ajaxServlet", urlPatterns = {"/vnpayajax", "/vnpayajax/*"})
public class ajaxServlet extends HttpServlet {

    private final VnPayService vnPayService = new VnPayService();
    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String amountParam = req.getParameter("amount");
        long amountVnd = 10000; // default test amount

        if (amountParam != null && !amountParam.isBlank()) {
            try {
                amountVnd = Long.parseLong(amountParam.trim());
            } catch (NumberFormatException e) {
                amountVnd = 10000;
            }
        } else {
            Cart cart = CookieUtil.getSyncedCart(req, productService);
            if (cart != null && !cart.getItems().isEmpty()) {
                double total = cart.getTotalAmount();
                // If total looks like USD price (< 1000), convert roughly to VND (1 USD ~ 25,000 VND)
                amountVnd = total < 1000 ? Math.round(total * 25000) : Math.round(total);
            }
        }

        String bankCode = req.getParameter("bankCode");
        String language = req.getParameter("language");
        if (language == null || language.isBlank()) {
            language = "vn";
        }

        String txnRef = req.getParameter("order_id");
        if (txnRef == null || txnRef.isBlank()) {
            txnRef = req.getParameter("txnRef");
        }
        if (txnRef == null || txnRef.isBlank()) {
            txnRef = VnPayConfig.getRandomNumber(8);
        }

        String orderInfo = req.getParameter("orderInfo");
        if (orderInfo == null || orderInfo.isBlank()) {
            orderInfo = "Thanh toan don hang:" + txnRef;
        }

        String ipAddr = VnPayConfig.getIpAddress(req);
        String returnUrl = VnPayConfig.vnp_ReturnUrl;

        String paymentUrl = vnPayService.createPaymentUrl(amountVnd, bankCode, orderInfo, txnRef, ipAddr, returnUrl, language);

        String acceptHeader = req.getHeader("Accept");
        String requestedWith = req.getHeader("X-Requested-With");
        boolean isAjax = (requestedWith != null && "XMLHttpRequest".equalsIgnoreCase(requestedWith))
                || (acceptHeader != null && acceptHeader.contains("application/json"));

        if (isAjax) {
            resp.setContentType("application/json;charset=UTF-8");
            String jsonResponse = String.format("{\"code\":\"00\",\"message\":\"success\",\"data\":\"%s\"}", paymentUrl);
            resp.getWriter().write(jsonResponse);
        } else {
            resp.sendRedirect(paymentUrl);
        }
    }
}
