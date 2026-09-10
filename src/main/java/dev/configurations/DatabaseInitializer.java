package dev.configurations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitializer {

    private static volatile boolean initialized = false;

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        SupabaseConfig cfg = SupabaseConfig.getInstance();
        try {
            Class.forName("org.postgresql.Driver");
            try (Connection conn = DriverManager.getConnection(cfg.getJdbcUrl(), cfg.getDbUsername(), cfg.getDbPassword());
                 Statement stmt = conn.createStatement()) {

                // 0. Tạo bảng users & nâng cấp schema
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id BIGSERIAL PRIMARY KEY,
                        username VARCHAR(100),
                        password VARCHAR(255),
                        first_name VARCHAR(255),
                        last_name VARCHAR(255),
                        email VARCHAR(255) NOT NULL,
                        date_of_birth VARCHAR(100),
                        heard_from VARCHAR(100),
                        wants_updates VARCHAR(50),
                        email_announcements VARCHAR(50),
                        contact_by VARCHAR(100)
                    );
                    ALTER TABLE users ADD COLUMN IF NOT EXISTS username VARCHAR(100);
                    ALTER TABLE users ADD COLUMN IF NOT EXISTS password VARCHAR(255);
                """);

                // 1. Tạo bảng products & seed dữ liệu mẫu nếu chưa có
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS products (
                        id BIGSERIAL PRIMARY KEY,
                        code VARCHAR(255) NOT NULL UNIQUE,
                        name VARCHAR(255) NOT NULL,
                        price DOUBLE PRECISION NOT NULL,
                        description TEXT
                    );
                    INSERT INTO products (code, name, price, description) VALUES
                    ('8601', '86 (the band) - True Life Songs and Pictures', 14.95, 'Album đĩa CD của nhóm 86'),
                    ('pf01', 'Paddlefoot - The First CD Album', 12.95, 'Album ca nhạc Paddlefoot đầu tay'),
                    ('jr01', 'Joe Rut - Genuine Wood Grained Finish', 14.95, 'Đĩa CD Joe Rut chính hãng'),
                    ('jsp01', 'Murach''s Java Servlets and JSP (4th Edition)', 54.50, 'Sách lập trình Java Servlet & JSP chuẩn Murach'),
                    ('kb01', 'RGB Mechanical Gaming Keyboard', 45.00, 'Bàn phím cơ chơi game LED RGB')
                    ON CONFLICT (code) DO NOTHING;
                """);

                // 2. Tạo bảng orders
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS orders (
                        id BIGSERIAL PRIMARY KEY,
                        order_number VARCHAR(255) NOT NULL UNIQUE,
                        customer_identifier VARCHAR(255),
                        order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        total_amount DOUBLE PRECISION NOT NULL,
                        status VARCHAR(50) NOT NULL DEFAULT 'COMPLETED'
                    );
                """);

                // 3. Tạo bảng order_details
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS order_details (
                        id BIGSERIAL PRIMARY KEY,
                        order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                        product_id BIGINT REFERENCES products(id),
                        product_code VARCHAR(255) NOT NULL,
                        product_name VARCHAR(255) NOT NULL,
                        unit_price DOUBLE PRECISION NOT NULL,
                        quantity INTEGER NOT NULL,
                        line_total DOUBLE PRECISION NOT NULL
                    );
                """);

                // 4. Tạo bảng bills
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS bills (
                        id BIGSERIAL PRIMARY KEY,
                        bill_number VARCHAR(255) NOT NULL UNIQUE,
                        order_id BIGINT NOT NULL UNIQUE REFERENCES orders(id) ON DELETE CASCADE,
                        bill_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        total_amount DOUBLE PRECISION NOT NULL,
                        payment_method VARCHAR(100) NOT NULL DEFAULT 'Mock Payment',
                        payment_status VARCHAR(50) NOT NULL DEFAULT 'PAID'
                    );
                """);

                System.out.println("[DatabaseInitializer] Database tables initialized successfully on PostgreSQL.");
                initialized = true;
            }
        } catch (Exception e) {
            System.err.println("[DatabaseInitializer] Error initializing database tables: " + e.getMessage());
        }
    }
}
