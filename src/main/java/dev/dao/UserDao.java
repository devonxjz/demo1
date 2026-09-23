package dev.dao;

import dev.configurations.JpaUtil;
import dev.models.User;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

/**
 * DAO dành riêng cho Entity User
 */
public class UserDao extends BaseDao<User, Long> {

    public UserDao() {
        super(User.class);
    }

    public Optional<User> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        EntityManager em = JpaUtil.getEntityManager();
        if (em == null) {
            return Optional.empty();
        }
        try {
            String jpql = "SELECT u FROM User u WHERE LOWER(u.username) = LOWER(:username)";
            List<User> list = em.createQuery(jpql, User.class)
                    .setParameter("username", username.trim())
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } catch (Exception e) {
            System.err.println("[UserDao] Lỗi findByUsername: " + e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public Optional<User> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        EntityManager em = JpaUtil.getEntityManager();
        if (em == null) {
            return Optional.empty();
        }
        try {
            String jpql = "SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)";
            List<User> list = em.createQuery(jpql, User.class)
                    .setParameter("email", email.trim())
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } catch (Exception e) {
            System.err.println("[UserDao] Lỗi findByEmail: " + e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public Optional<User> findByUsernameOrEmail(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return Optional.empty();
        }
        EntityManager em = JpaUtil.getEntityManager();
        if (em == null) {
            return Optional.empty();
        }
        try {
            String jpql = "SELECT u FROM User u WHERE LOWER(u.username) = LOWER(:val) OR LOWER(u.email) = LOWER(:val)";
            List<User> list = em.createQuery(jpql, User.class)
                    .setParameter("val", identifier.trim())
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } catch (Exception e) {
            System.err.println("[UserDao] Lỗi findByUsernameOrEmail: " + e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }
}
