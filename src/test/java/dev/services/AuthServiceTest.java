package dev.services;

import dev.models.User;
import dev.repositories.UserRepository;
import dev.utils.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private AuthService authService;
    private final List<User> database = new ArrayList<>();

    @BeforeEach
    void setUp() {
        database.clear();

        // Sử dụng FakeUserRepository để kiểm thử unit test độc lập không phụ thuộc DB mạng
        UserRepository fakeRepo = new UserRepository() {
            @Override
            public boolean save(User entity) {
                if (entity.getId() == null) {
                    entity.setId((long) (database.size() + 1));
                }
                database.add(entity);
                return true;
            }

            @Override
            public Optional<User> findByUsername(String username) {
                return database.stream()
                        .filter(u -> u.getUsername().equalsIgnoreCase(username))
                        .findFirst();
            }

            @Override
            public Optional<User> findByEmail(String email) {
                return database.stream()
                        .filter(u -> u.getEmail().equalsIgnoreCase(email))
                        .findFirst();
            }

            @Override
            public Optional<User> findByUsernameOrEmail(String identifier) {
                return database.stream()
                        .filter(u -> u.getUsername().equalsIgnoreCase(identifier) || u.getEmail().equalsIgnoreCase(identifier))
                        .findFirst();
            }

            @Override
            public boolean existsByUsername(String username) {
                return findByUsername(username).isPresent();
            }

            @Override
            public boolean existsByEmail(String email) {
                return findByEmail(email).isPresent();
            }
        };

        authService = new AuthService(fakeRepo);
    }

    @Test
    void testRegisterSuccessWithBCryptHash() {
        AuthService.RegisterResult result = authService.register(
                "devon",
                "devon@example.com",
                "secret123",
                "secret123",
                "Devon",
                "Nguyen"
        );

        assertTrue(result.success());
        assertNotNull(result.user());
        assertEquals("devon", result.user().getUsername());
        assertEquals("devon@example.com", result.user().getEmail());
        // Mật khẩu phải được băm BCrypt chứ không lưu plain text
        assertNotEquals("secret123", result.user().getPassword());
        assertTrue(result.user().getPassword().startsWith("$2a$") || result.user().getPassword().startsWith("$2"));
        assertTrue(PasswordUtil.checkPassword("secret123", result.user().getPassword()));
    }

    @Test
    void testRegisterFailsWhenPasswordMismatch() {
        AuthService.RegisterResult result = authService.register(
                "devon2",
                "devon2@example.com",
                "password123",
                "differentPassword",
                "Devon",
                "Nguyen"
        );

        assertFalse(result.success());
        assertEquals("Xác nhận mật khẩu không trùng khớp.", result.message());
    }

    @Test
    void testRegisterFailsWhenShortPassword() {
        AuthService.RegisterResult result = authService.register(
                "devon3",
                "devon3@example.com",
                "123",
                "123",
                "Devon",
                "Nguyen"
        );

        assertFalse(result.success());
        assertEquals("Mật khẩu phải có ít nhất 6 ký tự.", result.message());
    }

    @Test
    void testRegisterFailsWhenDuplicateUsername() {
        authService.register("devon", "devon1@example.com", "secret123", "secret123", "A", "B");
        AuthService.RegisterResult result = authService.register("devon", "devon2@example.com", "secret123", "secret123", "A", "B");

        assertFalse(result.success());
        assertTrue(result.message().contains("đã được sử dụng"));
    }

    @Test
    void testLoginSuccessWithUsernameOrEmail() {
        authService.register("testuser", "test@domain.com", "mypassword", "mypassword", "Test", "User");

        // Đăng nhập bằng username
        Optional<User> byUsername = authService.login("testuser", "mypassword");
        assertTrue(byUsername.isPresent());
        assertEquals("testuser", byUsername.get().getUsername());

        // Đăng nhập bằng email
        Optional<User> byEmail = authService.login("test@domain.com", "mypassword");
        assertTrue(byEmail.isPresent());
        assertEquals("testuser", byEmail.get().getUsername());
    }

    @Test
    void testLoginFailsWithWrongPassword() {
        authService.register("testuser2", "test2@domain.com", "correctPass", "correctPass", "Test", "User");

        Optional<User> failed = authService.login("testuser2", "wrongPass");
        assertTrue(failed.isEmpty());
    }
}
