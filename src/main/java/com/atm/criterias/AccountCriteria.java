package com.atm.criterias;

import com.atm.model.dtos.payloads.requests.AccountCriteriaRequest;
import com.atm.model.dtos.payloads.responses.AccountDto;
import com.atm.model.dtos.payloads.responses.AccountPageImplRes;
import com.atm.model.entities.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

/*
CriteriaBuilder query includes:
select, from, where, order by, group by
and all SQL commands
 */

@Slf4j
@Repository @AllArgsConstructor
public class AccountCriteria {
    // The EntityManager interface of Hibernate is responsible of creating queries

    private EntityManager entityManager;

    // Account dynamic criteria query method
    // I will get a request object where i can check & filter as I wish
    public Page<AccountDto> findAllPaginatedAndFiltered(
        Pageable pageable, AccountCriteriaRequest req
    ) {
        // A class to help building dynamic query
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        // Where the data will be populated
        CriteriaQuery<AccountDto> query = builder.createQuery(AccountDto.class);
        // where the data will be fetched from// FROM clause 1st query
        // One per query
        Root<Account> root = query.from(Account.class);
        // Dynamic parameters
        List<Predicate> predicates = new ArrayList<>();

        // Select slug, number, currency, balance, type, created_date From Account
        query.select(builder.construct(
                // where to
                AccountDto.class,
                // Fields of my Response class, The order matters as they are ordered inside the class.
                root.get("slug"), root.get("currency"), root.get("number"),
                root.get("balance"), root.get("type"), root.get("createdDate")
        ));

        // Where clause: (type like %search% or number like %search% or currency like %search%)
        if (!req.getSearchQuery().isEmpty() && !req.getSearchQuery().isBlank()) {
            log.info("Search query: {}", req.getSearchQuery());
            Predicate orPredicate = builder.or(
                    builder.like(root.get("number"), "%"+ req.getSearchQuery() + "%"),
                    builder.like(root.get("type"), "%"+ req.getSearchQuery() +"%"),
                    builder.like(root.get("currency"), "%" + req.getSearchQuery() + "%")
            );
            predicates.add(orPredicate);
        }

        // Where clause: and created_date between startDate and endDate
         predicates.add(
                builder.between(
                        root.get("createdDate"), req.getStartDate(), req.getEndDate()
                )
        );

        // Where clause: and user_id = userId
        predicates.add(
                builder.equal(root.get("user").get("id"), req.getUserId())
        );

        // building query with combining predicates
        query.where(predicates.toArray(new Predicate[0]));

        // Implement Sort & Paging

        // Sorting data
        if (req.getSortOrder().equalsIgnoreCase("asc")){
            query.orderBy(builder.asc(root.get(req.getSortBy())));
        } else {
            query.orderBy(builder.desc(root.get(req.getSortBy())));
        }

        // Used for type-safe casting from List<Object> to List<AccountDto>
        TypedQuery<AccountDto> typedQuery = entityManager.createQuery(query);
        typedQuery.getResultList().forEach(result -> log.info("Result: {}", result.getType()));



        // Paging
        // totalPages, totalElements, content, currentPage
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        // Execute first query and get result list
        List<AccountDto> accounts = typedQuery.getResultList();
        // totalPages & totalElements 2nd query
        // Must declare another Root for new query
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<Account> countRoot = countQuery.from(Account.class);
        // select count from account
        countQuery.select(builder.count(countRoot));
        // where user_id = userId
        countQuery.where(builder.equal(countRoot.get("user").get("id"), req.getUserId()));
        /*
        No need to use TypedQuery<> cuz i am expecting only one result,
        and no need for Type casting safety.
         */
        Long totalElements = entityManager.createQuery(countQuery).getSingleResult();
        int totalPages = (int) ((totalElements + pageable.getPageSize() - 1) / pageable.getPageSize());
        return new AccountPageImplRes<>(totalPages, totalElements, accounts, pageable);
    }
}
