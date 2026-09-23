package dev.dao;

import dev.configurations.JpaUtil;
import dev.models.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Optional;

/**
 * DAO dành riêng cho Entity Product
 */
public class ProductDao extends BaseDao<Product, Long> {

    public ProductDao() {
        super(Product.class);
    }

    public Optional<Product> findByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return Optional.empty();
        }
        EntityManager em = JpaUtil.getEntityManager();
        if (em == null) {
            return Optional.empty();
        }
        try {
            String jpql = "SELECT p FROM Product p WHERE LOWER(p.code) = LOWER(:code)";
            Product product = em.createQuery(jpql, Product.class)
                    .setParameter("code", code.trim())
                    .getSingleResult();
            return Optional.ofNullable(product);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("[ProductDao] Error finding product by code: " + e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}
