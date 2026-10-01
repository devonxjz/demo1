package com.vnpay.common;

import dev.configurations.VnPayConfig;
import dev.services.VnPayService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "vnpayQuery", urlPatterns = {"/vnpayquery", "/vnpayquery/*"})
public class vnpayQuery extends HttpServlet {

    private final VnPayService vnPayService = new VnPayService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String txnRef = req.getParameter("order_id");
        String transDate = req.getParameter("trans_date");
        String ipAddr = VnPayConfig.getIpAddress(req);

        resp.setContentType("application/json;charset=UTF-8");
        try {
            String result = vnPayService.queryTransaction(txnRef, transDate, ipAddr);
            resp.getWriter().write(result);
        } catch (Exception e) {
            resp.getWriter().write(String.format("{\"RspCode\":\"99\",\"Message\":\"Error: %s\"}", e.getMessage()));
        }
    }
}
