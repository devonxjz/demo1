package dev.configurations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatabaseConfigTest {

    @Test
    void testDatabaseConfigLoad() {
        DatabaseConfig config = DatabaseConfig.getInstance();
        assertNotNull(config);
        assertNotNull(config.getJdbcUrl());
        assertNotNull(config.getDbUsername());
        assertNotNull(config.getDbPassword());
    }

    @Test
    void testHikariCPConfigLoad() {
        HikariCPConfig config = HikariCPConfig.getInstance();
        assertNotNull(config);
        assertNotNull(config.getProperties());
        assertFalse(config.getProperties().isEmpty());
        assertNotNull(config.getProperties().getProperty("hibernate.hikari.minimumIdle"));
        assertNotNull(config.getProperties().getProperty("hibernate.hikari.maximumPoolSize"));
    }
}
