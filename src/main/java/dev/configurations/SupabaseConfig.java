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
            }
        } catch (Exception e) {
            System.err.println("[SupabaseConfig] Error loading env file: " + e.getMessage());
        }
    }
}
