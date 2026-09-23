package dev.services;

import dev.dao.UserDao;
import dev.models.User;
import dev.utils.PasswordUtil;

import java.util.Optional;

/**
 * Service xử lý xác thực và đăng ký người dùng
 */
public class AuthService {

    private final UserDao userDao;

    public AuthService() {
        this.userDao = new UserDao();
    }

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Kết quả xử lý đăng ký tài khoản
     */
    public record RegisterResult(boolean success, String message, User user) {
        public static RegisterResult success(User user) {
            return new RegisterResult(true, "Đăng ký tài khoản thành công!", user);
        }

        public static RegisterResult failure(String message) {
            return new RegisterResult(false, message, null);
        }
    }

    /**
     * Đăng ký tài khoản mới với mật khẩu được băm BCrypt.
     */
    public RegisterResult register(String username, String email, String password, String confirmPassword,
                                   String firstName, String lastName) {
        if (username == null || username.trim().isEmpty()) {
            return RegisterResult.failure("Vui lòng nhập tên đăng nhập (Username).");
        }
        username = username.trim();
        if (username.length() < 3) {
            return RegisterResult.failure("Tên đăng nhập phải có ít nhất 3 ký tự.");
        }

        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            return RegisterResult.failure("Vui lòng nhập email hợp lệ.");
        }
        email = email.trim();

        if (password == null || password.length() < 6) {
            return RegisterResult.failure("Mật khẩu phải có ít nhất 6 ký tự.");
        }

        if (confirmPassword != null && !password.equals(confirmPassword)) {
            return RegisterResult.failure("Xác nhận mật khẩu không trùng khớp.");
        }

        if (userDao.existsByUsername(username)) {
            return RegisterResult.failure("Tên đăng nhập '" + username + "' đã được sử dụng.");
        }

        if (userDao.existsByEmail(email)) {
            return RegisterResult.failure("Email '" + email + "' đã được đăng ký.");
        }

        // Băm mật khẩu bằng BCrypt
        String hashedPassword = PasswordUtil.hashPassword(password);

        User newUser = new User(username, email, hashedPassword,
                firstName != null ? firstName.trim() : "",
                lastName != null ? lastName.trim() : "");

        boolean saved = userDao.save(newUser);
        if (!saved) {
            return RegisterResult.failure("Không thể lưu tài khoản vào cơ sở dữ liệu. Vui lòng thử lại.");
        }

        return RegisterResult.success(newUser);
    }

    /**
     * Đăng nhập với Username hoặc Email kèm Mật khẩu thuần để kiểm tra BCrypt.
     */
    public Optional<User> login(String identifier, String plainPassword) {
        if (identifier == null || identifier.trim().isEmpty() ||
            plainPassword == null || plainPassword.isEmpty()) {
            return Optional.empty();
        }

        Optional<User> userOpt = userDao.findByUsernameOrEmail(identifier.trim());
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return Optional.empty();
        }

        if (PasswordUtil.checkPassword(plainPassword, user.getPassword())) {
            return Optional.of(user);
        }

        return Optional.empty();
    }
}
