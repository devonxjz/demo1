package dev.configurations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SupabaseConfigTest {

    @Test
    void testConfigLoad() {
        SupabaseConfig config = SupabaseConfig.getInstance();
        assertNotNull(config);
        assertNotNull(config.getJdbcUrl());
        assertNotNull(config.getHikariMinimumIdle());
        assertNotNull(config.getHikariMaximumPoolSize());
    }
}
