package com.atm.dao.criterias;

import com.atm.model.dtos.payloads.records.requests.TransactionsCriteriaRequest;
import com.atm.model.dtos.payloads.responses.UserAccountTransaction;
import com.atm.model.dtos.payloads.responses.TransactionsPageImpRes;
import com.atm.model.entities.Account;
import com.atm.model.entities.Transaction;
import com.atm.model.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * This is code documentation
 */
@Repository
@RequiredArgsConstructor
public class TransactionsCriteria {

    private final EntityManager entityManager;

    // Create data object for method params.
    public Page<UserAccountTransaction> findAll(TransactionsCriteriaRequest
                                                        request) {
        // TODO: search 4 if i am using docker mysql in local, will it affect remote one?
        // Helper to build dynamic query
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        // Predicates for dynamic query
        List<Predicate> predicates = new ArrayList<>();
        // Where data will be populated
        CriteriaQuery<UserAccountTransaction> query = builder
                .createQuery(UserAccountTransaction.class);

        // @Query: from Transaction
        // User root
        // Had to inverse the Root from User to Transaction
        // to easily reuse filters across queries
        Root<Transaction> transactionRoot = query.from(Transaction.class);
        // @Query: join Account
        // Join accounts // the names in the query are for Java model fields names
        Join<Transaction, Account> accountsJoin =
                transactionRoot.join("account");
        // @Query: join User (Preload)
        // Join transactions
        Join<Account, User> userJoin =
                accountsJoin.join("user");
        // @Query: select (u.firstName+' '+u.lastName) as fullName,
        // u.email, a.type, t.type, t.date, t.amount, t.balanceAfter
        query.select(
                builder.construct(
                        UserAccountTransaction.class,
                        transactionRoot.get("balanceAfter"),
                        transactionRoot.get("amount"),
                        transactionRoot.get("createdDate"),
                        transactionRoot.get("type"),
                        accountsJoin.get("type"),
                        userJoin.get("email"),
                        builder.concat(
                                // concat(firstName+" ", lastName)
                                builder.concat(userJoin.get("firstName"), " "),
                                userJoin.get("lastName")
                        )
                )
        );
        // Retrieving predicates
        predicates = getFilters(transactionRoot, accountsJoin, userJoin, request);
        //Apply filters and Retrieving list of data
        query.where(predicates.toArray(new Predicate[0]));
        // Sort: default is createdDate descending
        // Sorting is set by service layer
        if (request.sortOrder().equalsIgnoreCase("asc")) {
            if (isAccountField(request.sortBy()))
                query.orderBy(builder.asc(accountsJoin.get("type")));
            if (!isTransactionField(request.sortBy()).isEmpty())
                query.orderBy(builder.asc(transactionRoot.get(
                        request.sortBy()
                )));
            if (isUserField(request.sortBy()))
                query.orderBy(builder.asc(userJoin.get(
                        request.sortBy()
                )));
        } else {
            if (isAccountField(request.sortBy()))
                query.orderBy(builder.desc(accountsJoin.get("type")));
            if (!isTransactionField(request.sortBy()).isEmpty())
                query.orderBy(builder.desc(transactionRoot.get(
                        request.sortBy()
                )));
            if (isUserField(request.sortBy()))
                query.orderBy(builder.desc(userJoin.get(
                        request.sortBy()
                )));
        }
        // Generating query, and preparing for execution
        // Paging, is handled as separated query
        TypedQuery<UserAccountTransaction> typedQuery =
                entityManager.createQuery(query);
        // Count query & paging
        // Current page
        typedQuery.setFirstResult((int) request.pageable().getOffset());
        // Limit
        typedQuery.setMaxResults(request.pageable().getPageSize());
        // Executing 1st query
        List<UserAccountTransaction> loadedTransactions = typedQuery
                .getResultList();
        System.out.println("Returned List of items size:" + loadedTransactions
                .size());
        // Count query's result // for accurate paging
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        // Root for count >> Transaction
        Root<Transaction> countRoot = countQuery.from(Transaction.class);
        Join<Transaction, Account> accountJoin = countRoot.join("account");
        Join<Account, User> usersJoin = accountJoin.join("user");
        // Clearing filters, just in case.
        predicates.clear();
        predicates = getFilters(countRoot, accountJoin, usersJoin, request);
        // Executing final steps of paging & countQuery
        countQuery.select(builder.count(countRoot));
        countQuery.where(predicates.toArray(new Predicate[0]));
        Long totalElements = entityManager.createQuery(countQuery)
                .getSingleResult();
        System.out.println("Total elements: "+ totalElements);
        loadedTransactions.forEach(
                tr -> {
                    System.out.println(
                            tr.getFullName()+" "+
                                    tr.getAmount()
                    );
                }
        );
        int totalPages = (int) ((totalElements) + request.pageable().getPageSize() - 1)
                / request.pageable().getPageSize();
        return new TransactionsPageImpRes<>(totalPages, totalElements,
                loadedTransactions, request.pageable());
    }

    /**
     * If fieldName from transaction model,
     * and it is "type" returns "type" otherwise returns fieldName.
     * If not of Transaction model returns ""
     *
     * @param fieldName
     * @return
     */
    private String isTransactionField(String fieldName) {
        List<String> fields = List.of("trType", "createdDate", "amount",
                "balanceAfter");
        boolean found =  fields.stream().anyMatch(key -> key.contains(fieldName));
        if (found)
            return fieldName.equals("trType") ? "type" : fieldName;
        return "";
    }

    /**
     * If fieldName from account (type) model, returns true.
     *
     * @param fieldName
     * @return
     */
    private boolean isAccountField(String fieldName) {
        return fieldName.equals("acType");
    }

    /**
     * If fieldName from user model, returns true.
     *
     * @param fieldName
     * @return
     */
    private boolean isUserField(String fieldName) {
        List<String> fields = List.of("email", "firstName", "lastName");
        return fields.stream().anyMatch(value -> value.contains(fieldName));
    }

    /**
     * It is a reusable filters across queries.
     * @param transactionRoot
     * @param accountJoin
     * @param userJoin
     * @param request
     * @return
     */
    private List<Predicate> getFilters(Root<Transaction> transactionRoot,
                                       Join<Transaction, Account> accountJoin,
                                       Join<Account, User> userJoin,
                                       TransactionsCriteriaRequest request) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        List<Predicate> predicates = new ArrayList<>();
        // @Query: where u.firstName like %search% or u.lastName like %search%
        // or tr.type like %search% or ac.type like %search% or email
        // like %search%
        // 1st optional where clause
        if (!request.searchQuery().isEmpty() && !request.searchQuery().isBlank()) {
            Predicate orPredicate = builder
                    .or(
                            builder.like(
                                    builder.lower(userJoin.get("firstName")),
                                    "%" + request.searchQuery().toLowerCase() + "%"
                            ),
                            builder.like(
                                    builder.lower(userJoin.get("lastName")),
                                    "%" + request.searchQuery().toLowerCase() + "%"
                            ),
                            builder.like(
                                    builder.lower(userJoin.get("email")),
                                    "%" + request.searchQuery().toLowerCase() + "%"
                            ),
                            builder.like(
                                    builder.lower(transactionRoot.get("type")),
                                    "%" + request.searchQuery().toLowerCase() + "%"
                            ),
                            builder.like(
                                    builder.lower(accountJoin.get("type")),
                                    "%" + request.searchQuery().toLowerCase() + "%"
                            )

                    );
            // Registering orPredicate
            predicates.add(orPredicate);
        }
        // Where (between) Optional: amount attribute
        if (request.startAmount() != 0.0 && request.endAmount() != 0.0) {
            predicates.add(
                    builder.between(
                            transactionRoot.get("amount"),
                            request.startAmount(),
                            request.endAmount()
                    )
            );
        }
        // Where (between) Required: createdDate
        predicates.add(
                builder.between(
                        transactionRoot.get("createdDate"),
                        request.from(),
                        request.to()
                )
        );
        return predicates;
    }

}
