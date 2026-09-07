package dev.repositories;

import dev.configurations.JpaUtil;
import dev.models.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Optional;

public class OrderRepository extends BaseRepository<Order, Long> {

    public OrderRepository() {
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
            Order order = em.createQuery(jpql, Order.class)
                    .setParameter("orderNumber", orderNumber.trim())
                    .getSingleResult();
            return Optional.ofNullable(order);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("[OrderRepository] Error finding order by number: " + e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}
