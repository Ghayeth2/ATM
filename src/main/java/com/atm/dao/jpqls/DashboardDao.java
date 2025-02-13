package com.atm.dao.jpqls;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Objects;

@Repository
@AllArgsConstructor
public class DashboardDao {
    private EntityManager em;
    public long countUsers(String firstName, String lastName
    , LocalDateTime from, LocalDateTime to) {
        StringBuilder jpql = new StringBuilder(
                "SELECT count(u) FROM User u where u.createdDate between " +
                        ":from and :to"
        );

        if (firstName != null && !firstName.isEmpty()) {
            jpql.append(" and u.firstName like :firstName");
        }
        if (lastName != null && !lastName.isEmpty()) {
            jpql.append(" and u.lastName like :lastName");
        }

        TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);

        query.setParameter("from", from);
        query.setParameter("to", to);
        if (firstName != null && !firstName.isEmpty()) {
            query.setParameter("firstName", "%" + firstName + "%");
        }
        if (lastName != null && !lastName.isEmpty()) {
            query.setParameter("lastName", "%" + lastName + "%");
        }
        return query.getSingleResult();
    }

    public long countAccounts(long id, String type,
                             LocalDateTime from,
                             LocalDateTime to) {
        StringBuilder jpql = new StringBuilder(
                "select count(a) from Account a where a.createdDate between" +
                        ":from and :to"
        );
        if (id != 0) {
            jpql.append(" and a.user.id = :id");
        }
        if (type != null && !type.isEmpty()) {
            jpql.append(" and a.type = :type");
        }

        TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);

        query.setParameter("from", from);
        query.setParameter("to", to);
        if (id != 0) {
            query.setParameter("id", id);
        }
        if (type != null && !type.isEmpty()) {
            query.setParameter("type", type);
        }
        return query.getSingleResult();
    }

    public long countTransactions(long id,
                                 String accountType,
                                 String accountNumber,
                                 String transactionType,
                                 LocalDateTime from,
                                 LocalDateTime to) {
        StringBuilder jpql = new StringBuilder(
                "select count(t) from Transaction t"
        );
        if (id != 0 || (accountType != null && !accountType.isEmpty())
        || (accountNumber != null && !accountNumber.isEmpty())) {
            jpql.append(" join t.account a ");
        }

        jpql.append(" where t.createdDate between :from and :to");

        if (id != 0) {
            jpql.append(" and a.user.id = :id");
        }

        if (accountType != null && !accountType.isEmpty()) {
            jpql.append(" and a.type = :accountType");
        }

        if (accountNumber != null && !accountNumber.isEmpty()) {
            jpql.append(" and a.number = :accountNumber");
        }

        if (transactionType != null && !transactionType.isEmpty()) {
            jpql.append(" and t.type = :transactionType");
        }

        TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);

        query.setParameter("from", from);
        query.setParameter("to", to);
        if (id != 0) {
            query.setParameter("id", id);
        }
        if (accountType != null && !accountType.isEmpty()) {
            query.setParameter("accountType", accountType);
        }
        if (accountNumber != null && !accountNumber.isEmpty()) {
            query.setParameter("accountNumber", accountNumber);
        }
        if (transactionType != null && !transactionType.isEmpty()) {
            query.setParameter("transactionType", transactionType);
        }
        return query.getSingleResult();
    }
}
