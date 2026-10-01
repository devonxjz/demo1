package dev.services;

import dev.configurations.VnPayConfig;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Service xử lý tích hợp VNPAY (Tạo URL thanh toán, xác thực chữ ký, truy vấn & hoàn tiền)
 */
public class VnPayService {

    public String createPaymentUrl(long amountVnd, String bankCode, String orderInfo,
                                   String txnRef, String ipAddr, String returnUrl, String locale) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        long vnpAmount = amountVnd * 100;

        if (txnRef == null || txnRef.isBlank()) {
            txnRef = VnPayConfig.getRandomNumber(8);
        }
        if (orderInfo == null || orderInfo.isBlank()) {
            orderInfo = "Thanh toan don hang:" + txnRef;
        }
        if (ipAddr == null || ipAddr.isBlank()) {
            ipAddr = "127.0.0.1";
        }
        if (returnUrl == null || returnUrl.isBlank()) {
            VnPayConfig.loadConfig();
            returnUrl = VnPayConfig.vnp_ReturnUrl;
        }

        if (locale == null || locale.isBlank()) {
            locale = "vn";
        }

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", VnPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(vnpAmount));
        vnp_Params.put("vnp_CurrCode", "VND");

        if (bankCode != null && !bankCode.isBlank()) {
            vnp_Params.put("vnp_BankCode", bankCode.trim());
        }
        vnp_Params.put("vnp_TxnRef", txnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", locale);
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_IpAddr", ipAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                try {
                    // Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                    // Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                } catch (Exception ignored) {
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = VnPayConfig.hmacSHA512(VnPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        return VnPayConfig.vnp_PayUrl + "?" + queryUrl;
    }

    public boolean verifySignature(Map<String, String> fields, String secureHash) {
        if (secureHash == null || secureHash.isBlank() || fields == null) {
            return false;
        }
        Map<String, String> copy = new HashMap<>(fields);
        copy.remove("vnp_SecureHash");
        copy.remove("vnp_SecureHashType");
        String calculated = VnPayConfig.hashAllFields(copy);
        return calculated.equalsIgnoreCase(secureHash.trim());
    }

    public String queryTransaction(String txnRef, String transDate, String ipAddress) throws Exception {
        String vnp_RequestId = VnPayConfig.getRandomNumber(8);
        String vnp_Version = "2.1.0";
        String vnp_Command = "querydr";
        String vnp_TmnCode = VnPayConfig.vnp_TmnCode;
        String vnp_OrderInfo = "Kiem tra ket qua GD OrderId:" + txnRef;

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        if (ipAddress == null || ipAddress.isBlank()) {
            ipAddress = "127.0.0.1";
        }
        if (transDate == null || transDate.isBlank()) {
            transDate = vnp_CreateDate;
        }

        String hashData = String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode,
                txnRef, transDate, vnp_CreateDate, ipAddress, vnp_OrderInfo);
        String vnp_SecureHash = VnPayConfig.hmacSHA512(VnPayConfig.secretKey, hashData);

        String jsonPayload = String.format("{"
                        + "\"vnp_RequestId\":\"%s\","
                        + "\"vnp_Version\":\"%s\","
                        + "\"vnp_Command\":\"%s\","
                        + "\"vnp_TmnCode\":\"%s\","
                        + "\"vnp_TxnRef\":\"%s\","
                        + "\"vnp_OrderInfo\":\"%s\","
                        + "\"vnp_TransactionDate\":\"%s\","
                        + "\"vnp_CreateDate\":\"%s\","
                        + "\"vnp_IpAddr\":\"%s\","
                        + "\"vnp_SecureHash\":\"%s\""
                        + "}",
                escapeJson(vnp_RequestId),
                escapeJson(vnp_Version),
                escapeJson(vnp_Command),
                escapeJson(vnp_TmnCode),
                escapeJson(txnRef),
                escapeJson(vnp_OrderInfo),
                escapeJson(transDate),
                escapeJson(vnp_CreateDate),
                escapeJson(ipAddress),
                escapeJson(vnp_SecureHash)
        );

        return sendPostRequest(VnPayConfig.vnp_ApiUrl, jsonPayload);
    }

    public String refundTransaction(String txnRef, long amountVnd, String trantype,
                                    String transDate, String user, String ipAddress) throws Exception {
        String vnp_RequestId = VnPayConfig.getRandomNumber(8);
        String vnp_Version = "2.1.0";
        String vnp_Command = "refund";
        String vnp_TmnCode = VnPayConfig.vnp_TmnCode;
        if (trantype == null || trantype.isBlank()) {
            trantype = "02";
        }
        long vnpAmount = amountVnd * 100;
        String vnp_Amount = String.valueOf(vnpAmount);
        String vnp_OrderInfo = "Hoan tien GD OrderId:" + txnRef;
        String vnp_TransactionNo = "";

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        if (ipAddress == null || ipAddress.isBlank()) {
            ipAddress = "127.0.0.1";
        }
        if (transDate == null || transDate.isBlank()) {
            transDate = vnp_CreateDate;
        }
        if (user == null || user.isBlank()) {
            user = "Admin";
        }

        String hashData = String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode,
                trantype, txnRef, vnp_Amount, vnp_TransactionNo, transDate,
                user, vnp_CreateDate, ipAddress, vnp_OrderInfo);
        String vnp_SecureHash = VnPayConfig.hmacSHA512(VnPayConfig.secretKey, hashData);

        String jsonPayload = String.format("{"
                        + "\"vnp_RequestId\":\"%s\","
                        + "\"vnp_Version\":\"%s\","
                        + "\"vnp_Command\":\"%s\","
                        + "\"vnp_TmnCode\":\"%s\","
                        + "\"vnp_TransactionType\":\"%s\","
                        + "\"vnp_TxnRef\":\"%s\","
                        + "\"vnp_Amount\":\"%s\","
                        + "\"vnp_OrderInfo\":\"%s\","
                        + "\"vnp_TransactionNo\":\"%s\","
                        + "\"vnp_TransactionDate\":\"%s\","
                        + "\"vnp_CreateBy\":\"%s\","
                        + "\"vnp_CreateDate\":\"%s\","
                        + "\"vnp_IpAddr\":\"%s\","
                        + "\"vnp_SecureHash\":\"%s\""
                        + "}",
                escapeJson(vnp_RequestId),
                escapeJson(vnp_Version),
                escapeJson(vnp_Command),
                escapeJson(vnp_TmnCode),
                escapeJson(trantype),
                escapeJson(txnRef),
                escapeJson(vnp_Amount),
                escapeJson(vnp_OrderInfo),
                escapeJson(vnp_TransactionNo),
                escapeJson(transDate),
                escapeJson(user),
                escapeJson(vnp_CreateDate),
                escapeJson(ipAddress),
                escapeJson(vnp_SecureHash)
        );

        return sendPostRequest(VnPayConfig.vnp_ApiUrl, jsonPayload);
    }

    private String sendPostRequest(String endpoint, String jsonPayload) throws Exception {
        URL url = URI.create(endpoint).toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        con.setConnectTimeout(10000);
        con.setReadTimeout(15000);

        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            wr.flush();
        }

        int responseCode = con.getResponseCode();
        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                (responseCode >= 200 && responseCode < 400) ? con.getInputStream() : con.getErrorStream(),
                StandardCharsets.UTF_8))) {
            String output;
            while ((output = in.readLine()) != null) {
                response.append(output);
            }
        }
        return response.toString();
    }

    private String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
