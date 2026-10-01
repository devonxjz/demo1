package dev.services;

import dev.configurations.VnPayConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VnPayServiceTest {

    private VnPayService vnPayService;

    @BeforeEach
    void setUp() {
        vnPayService = new VnPayService();
    }

    @Test
    @DisplayName("Tạo payment URL chứa đầy đủ tham số và chữ ký hợp lệ")
    void testCreatePaymentUrl() {
        long amountVnd = 50000;
        String bankCode = "NCB";
        String orderInfo = "Thanh toan test don hang";
        String txnRef = "TEST_TXN_123";
        String ipAddr = "127.0.0.1";
        String returnUrl = "http://localhost:8080/demo1/vnpay_return.jsp";
        String locale = "vn";

        String paymentUrl = vnPayService.createPaymentUrl(amountVnd, bankCode, orderInfo, txnRef, ipAddr, returnUrl, locale);

        assertNotNull(paymentUrl);
        assertTrue(paymentUrl.startsWith(VnPayConfig.vnp_PayUrl));
        assertTrue(paymentUrl.contains("vnp_Amount=5000000")); // 50000 * 100
        assertTrue(paymentUrl.contains("vnp_BankCode=NCB"));
        assertTrue(paymentUrl.contains("vnp_TxnRef=TEST_TXN_123"));
        assertTrue(paymentUrl.contains("vnp_SecureHash="));
    }

    @Test
    @DisplayName("Xác thực chữ ký hợp lệ và phát hiện dữ liệu bị giả mạo")
    void testVerifySignature() {
        Map<String, String> fields = new HashMap<>();
        fields.put("vnp_Amount", "1000000");
        fields.put("vnp_BankCode", "NCB");
        fields.put("vnp_Command", "pay");
        fields.put("vnp_OrderInfo", "Thanh toan don hang: 12345");
        fields.put("vnp_ResponseCode", "00");
        fields.put("vnp_TmnCode", VnPayConfig.vnp_TmnCode);
        fields.put("vnp_TxnRef", "12345");

        String secureHash = VnPayConfig.hashAllFields(fields);
        assertNotNull(secureHash);
        assertFalse(secureHash.isEmpty());

        // Kiểm tra chữ ký gốc -> phải hợp lệ
        assertTrue(vnPayService.verifySignature(fields, secureHash));

        // Giả mạo số tiền -> phải thất bại
        Map<String, String> tampered = new HashMap<>(fields);
        tampered.put("vnp_Amount", "2000000");
        assertFalse(vnPayService.verifySignature(tampered, secureHash));

        // Chữ ký rỗng hoặc null -> phải trả về false
        assertFalse(vnPayService.verifySignature(fields, null));
        assertFalse(vnPayService.verifySignature(fields, ""));
    }

    @Test
    @DisplayName("Hàm HMAC SHA512 tạo ra kết quả chính xác, ổn định")
    void testHmacSHA512() {
        String key = "testkey";
        String data = "helloword";
        String hash1 = VnPayConfig.hmacSHA512(key, data);
        String hash2 = VnPayConfig.hmacSHA512(key, data);

        assertNotNull(hash1);
        assertEquals(128, hash1.length()); // SHA-512 hex string length = 128
        assertEquals(hash1, hash2);
    }
}
