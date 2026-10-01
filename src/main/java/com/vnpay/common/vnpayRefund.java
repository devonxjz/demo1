package com.vnpay.common;

import dev.configurations.VnPayConfig;
import dev.services.VnPayService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "vnpayRefund", urlPatterns = {"/vnpayrefund", "/vnpayrefund/*"})
public class vnpayRefund extends HttpServlet {

    private final VnPayService vnPayService = new VnPayService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String txnRef = req.getParameter("order_id");
        String amountParam = req.getParameter("amount");
        long amountVnd = 10000;
        if (amountParam != null && !amountParam.isBlank()) {
            try {
                amountVnd = Long.parseLong(amountParam.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        String tranType = req.getParameter("trantype");
        String transDate = req.getParameter("trans_date");
        String user = req.getParameter("user");
        String ipAddr = VnPayConfig.getIpAddress(req);

        resp.setContentType("application/json;charset=UTF-8");
        try {
            String result = vnPayService.refundTransaction(txnRef, amountVnd, tranType, transDate, user, ipAddr);
            resp.getWriter().write(result);
        } catch (Exception e) {
            resp.getWriter().write(String.format("{\"RspCode\":\"99\",\"Message\":\"Error: %s\"}", e.getMessage()));
        }
    }
}
