package dev.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    private static final int LOG_ROUNDS = 12;

    /**
     * Băm mật khẩu thuần sử dụng thuật toán BCrypt với muối (salt) an toàn.
     *
     * @param plainPassword Mật khẩu người dùng nhập
     * @return Chuỗi mã băm BCrypt ($2a$...)
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống khi băm.");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(LOG_ROUNDS));
    }

    /**
     * Kiểm tra mật khẩu thuần nhập vào có khớp với chuỗi băm BCrypt hay không.
     *
     * @param plainPassword  Mật khẩu người dùng nhập
     * @param hashedPassword Chuỗi mật khẩu đã băm trong CSDL
     * @return true nếu khớp, ngược lại false
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            System.err.println("[PasswordUtil] Lỗi xác thực mật khẩu: " + e.getMessage());
            return false;
        }
    }
}
