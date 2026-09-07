package dev.repositories;

import dev.configurations.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @param <T>
 * @param <ID>
 */
public abstract class BaseRepository<T, ID> {

    protected final Class<T> entityClass;

    protected BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public boolean save(T entity) {
        EntityManager em = JpaUtil.getEntityManager();
        if (em == null) {
            return false;
        }
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            System.err.println("[" + getClass().getSimpleName() + "] Error saving entity: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    public Optional<T> findById(ID id) {
        EntityManager em = JpaUtil.getEntityManager();
        if (em == null) {
            return Optional.empty();
        }
        try {
            T entity = em.find(entityClass, id);
            return Optional.ofNullable(entity);
        } finally {
            em.close();
        }
    }

    public List<T> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        if (em == null) {
            return Collections.emptyList();
        }
        try {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            return em.createQuery(jpql, entityClass).getResultList();
        } catch (Exception e) {
            System.err.println("[" + getClass().getSimpleName() + "] Error finding all entities: " + e.getMessage());
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    public boolean delete(T entity) {
        EntityManager em = JpaUtil.getEntityManager();
        if (em == null) {
            return false;
        }
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.remove(em.contains(entity) ? entity : em.merge(entity));
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            System.err.println("[" + getClass().getSimpleName() + "] Error deleting entity: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    public boolean deleteById(ID id) {
        EntityManager em = JpaUtil.getEntityManager();
        if (em == null) {
            return false;
        }
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            System.err.println("[" + getClass().getSimpleName() + "] Error deleting by id: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
}
