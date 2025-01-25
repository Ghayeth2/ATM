package com.atm.dao.daos;

import com.atm.model.dtos.payloads.records.responses.TransactionDto;
import com.atm.model.entities.Transaction;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface TransactionDao extends JpaRepository<Transaction, Long> {
    // TODO: JPQL query for returning account's movements & filtering with Type.
    @Query("select new com.atm.model.dtos.payloads.records.responses.TransactionDto(t.slug, t.type, " +
            "t.createdDate, t.receiptUrl, t.amount, t.balanceAfter) " +
            "from Transaction t where t.account.id = :accountId and " +
            "t.createdDate between :startDate and :endDate")
    Page<TransactionDto> findAllByAccount
    (@Param("accountId") Long accountId,
     @Param("startDate") LocalDateTime startDate,
     @Param("endDate") LocalDateTime endDate,
     Pageable pageable);

}
