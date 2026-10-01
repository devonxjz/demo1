package dev.configurations;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

@Getter
public class VnPayConfig {

    private static final VnPayConfig INSTANCE = new VnPayConfig();

    public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static String vnp_ReturnUrl = "http://localhost:8088/vnpay_return.jsp";
    public static String vnp_TmnCode = "N57OM2H4";
    public static String secretKey = "OWHGBKLAMWRPACIYTQPINXSRSBMWNHSK";
    public static String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

    public static VnPayConfig getInstance() {
        return INSTANCE;
    }

    static {
        loadConfig();
    }

    public static void loadConfig() {
        Properties props = new Properties();
        try (InputStream in = VnPayConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception ignored) {
        }

        vnp_PayUrl = getProp(props, "VNPAY_PAY_URL", "vnpay.pay.url", vnp_PayUrl);
        vnp_ReturnUrl = getProp(props, "VNPAY_RETURN_URL", "vnpay.return.url", vnp_ReturnUrl);
        vnp_TmnCode = getProp(props, "VNPAY_TMN_CODE", "vnpay.tmn.code", vnp_TmnCode);
        secretKey = getProp(props, "VNPAY_HASH_SECRET", "vnpay.hash.secret", secretKey);
        vnp_ApiUrl = getProp(props, "VNPAY_API_URL", "vnpay.api.url", vnp_ApiUrl);
    }

    private static String getProp(Properties props, String envKey, String propKey, String defaultVal) {
        String env = System.getenv(envKey);
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        if (props != null) {
            String val = props.getProperty(propKey);
            if (val != null && !val.isBlank()) {
                return val.trim();
            }
        }
        return defaultVal;
    }

    public static String md5(String message) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(message.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            return "";
        }
    }

    public static String Sha256(String message) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(message.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            return "";
        }
    }

    public static String hashAllFields(Map<String, String> fields) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                sb.append(fieldName);
                sb.append("=");
                sb.append(fieldValue);
            }
            if (itr.hasNext()) {
                sb.append("&");
            }
        }
        return hmacSHA512(secretKey, sb.toString());
    }

    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKeySpec = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKeySpec);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress;
        try {
            ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null || ipAddress.isBlank()) {
                ipAddress = request.getRemoteAddr();
            } else {
                // If forwarded through proxies, get the first IP
                if (ipAddress.contains(",")) {
                    ipAddress = ipAddress.split(",")[0].trim();
                }
            }
        } catch (Exception e) {
            ipAddress = "127.0.0.1";
        }
        return (ipAddress != null && !ipAddress.isBlank()) ? ipAddress : "127.0.0.1";
    }

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
