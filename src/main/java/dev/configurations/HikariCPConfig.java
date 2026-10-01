package dev.configurations;

import lombok.Getter;

import java.io.InputStream;
import java.util.Properties;

@Getter
public class HikariCPConfig {

    private static final HikariCPConfig INSTANCE = new HikariCPConfig();

    private final Properties properties = new Properties();

    public static HikariCPConfig getInstance() {
        return INSTANCE;
    }

    private HikariCPConfig() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                Properties allProps = new Properties();
                allProps.load(in);
                for (String name : allProps.stringPropertyNames()) {
                    if (name.startsWith("hibernate.hikari.")) {
                        this.properties.setProperty(name, allProps.getProperty(name));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[HikariCPConfig] Error loading properties: " + e.getMessage());
        }
    }
}
