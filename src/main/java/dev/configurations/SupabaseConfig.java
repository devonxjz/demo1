package dev.configurations;

import lombok.Getter;

import java.io.InputStream;
import java.util.Properties;

@Getter
public class SupabaseConfig {

    private static final SupabaseConfig INSTANCE = new SupabaseConfig();

    private String jdbcUrl = "";
    private String dbUsername = "postgres";
    private String dbPassword = "";

    // HikariCP Connection Pool properties
    private String hikariMinimumIdle = "2";
    private String hikariMaximumPoolSize = "10";
    private String hikariIdleTimeout = "30000";
    private String hikariConnectionTimeout = "20000";
    private String hikariMaxLifetime = "1800000";

    public static SupabaseConfig getInstance() {
        return INSTANCE;
    }

    private SupabaseConfig() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                this.jdbcUrl = props.getProperty("supabase.datasource.url", "");
                this.dbUsername = props.getProperty("supabase.datasource.username", "postgres");
                this.dbPassword = props.getProperty("supabase.datasource.password", "");

                this.hikariMinimumIdle = props.getProperty("hibernate.hikari.minimumIdle", "2");
                this.hikariMaximumPoolSize = props.getProperty("hibernate.hikari.maximumPoolSize", "10");
                this.hikariIdleTimeout = props.getProperty("hibernate.hikari.idleTimeout", "30000");
                this.hikariConnectionTimeout = props.getProperty("hibernate.hikari.connectionTimeout", "20000");
                this.hikariMaxLifetime = props.getProperty("hibernate.hikari.maxLifetime", "1800000");
            }
        } catch (Exception e) {
            System.err.println("[SupabaseConfig] Error loading env file: " + e.getMessage());
        }
    }
}
