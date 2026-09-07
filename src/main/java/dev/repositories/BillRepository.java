package dev.repositories;

import dev.configurations.JpaUtil;
import dev.models.Bill;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Optional;

public class BillRepository extends BaseRepository<Bill, Long> {

    public BillRepository() {
        super(Bill.class);
    }

    public Optional<Bill> findByBillNumber(String billNumber) {
        if (billNumber == null || billNumber.trim().isEmpty()) {
            return Optional.empty();
        }
        EntityManager em = JpaUtil.getEntityManager();
        if (em == null) {
            return Optional.empty();
        }
        try {
            String jpql = "SELECT b FROM Bill b WHERE b.billNumber = :billNumber";
            Bill bill = em.createQuery(jpql, Bill.class)
                    .setParameter("billNumber", billNumber.trim())
                    .getSingleResult();
            return Optional.ofNullable(bill);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("[BillRepository] Error finding bill by number: " + e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}
