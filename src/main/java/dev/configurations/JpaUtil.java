package dev.configurations;

import dev.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.Configuration;

public class JpaUtil {

    private static EntityManagerFactory emf;

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            try {
                DatabaseConfig dbCfg = DatabaseConfig.getInstance();
                HikariCPConfig poolCfg = HikariCPConfig.getInstance();
                emf = new Configuration()
                        .addAnnotatedClass(User.class)
                        .addAnnotatedClass(dev.models.Product.class)
                        .addAnnotatedClass(dev.models.Order.class)
                        .addAnnotatedClass(dev.models.OrderDetail.class)
                        .addAnnotatedClass(dev.models.Bill.class)
                        .addProperties(poolCfg.getProperties())
                        .setProperty("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider")
                        .setProperty("jakarta.persistence.jdbc.driver", "org.postgresql.Driver")
                        .setProperty("jakarta.persistence.jdbc.url", dbCfg.getJdbcUrl())
                        .setProperty("jakarta.persistence.jdbc.user", dbCfg.getDbUsername())
                        .setProperty("jakarta.persistence.jdbc.password", dbCfg.getDbPassword())
                        .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                        .setProperty("hibernate.boot.allow_jdbc_metadata_access", "false")
                        .setProperty("hibernate.hbm2ddl.auto", "none")
                        .buildSessionFactory();
            } catch (Exception e) {
                System.err.println("[JpaUtil] Hibernate init error: " + e.getMessage());
            }
        }
        return emf;
    }

    public static EntityManager getEntityManager() {
        EntityManagerFactory factory = getEntityManagerFactory();
        return (factory != null && factory.isOpen()) ? factory.createEntityManager() : null;
    }
}
