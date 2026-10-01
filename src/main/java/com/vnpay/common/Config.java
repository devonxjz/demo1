package com.vnpay.common;

import dev.configurations.VnPayConfig;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * Compatible bridge with original VNPAY sample Config class using Jakarta EE
 */
public class Config {

    public static String vnp_PayUrl = VnPayConfig.vnp_PayUrl;
    public static String vnp_ReturnUrl = VnPayConfig.vnp_ReturnUrl;
    public static String vnp_TmnCode = VnPayConfig.vnp_TmnCode;
    public static String secretKey = VnPayConfig.secretKey;
    public static String vnp_ApiUrl = VnPayConfig.vnp_ApiUrl;

    public static String md5(String message) {
        return VnPayConfig.md5(message);
    }

    public static String Sha256(String message) {
        return VnPayConfig.Sha256(message);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String hashAllFields(Map fields) {
        return VnPayConfig.hashAllFields((Map<String, String>) fields);
    }

    public static String hmacSHA512(final String key, final String data) {
        return VnPayConfig.hmacSHA512(key, data);
    }

    public static String getIpAddress(HttpServletRequest request) {
        return VnPayConfig.getIpAddress(request);
    }

    public static String getRandomNumber(int len) {
        return VnPayConfig.getRandomNumber(len);
    }
}
