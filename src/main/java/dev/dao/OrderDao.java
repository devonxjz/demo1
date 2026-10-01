package dev.dao;

import dev.configurations.JpaUtil;
import dev.models.Order;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class OrderDao extends BaseDao<Order, Long> {

    public OrderDao() {
        super(Order.class);
    }

    public Optional<Order> findByOrderNumber(String orderNumber) {
        if (orderNumber == null || orderNumber.trim().isEmpty()) {
            return Optional.empty();
        }
        EntityManager em = JpaUtil.getEntityManager();
        if (em == null) {
            return Optional.empty();
        }
        try {
            String jpql = "SELECT o FROM Order o WHERE o.orderNumber = :orderNumber";
            List<Order> list = em.createQuery(jpql, Order.class)
                    .setParameter("orderNumber", orderNumber.trim())
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } catch (Exception e) {
            System.err.println("[OrderDao] Error findByOrderNumber: " + e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}
