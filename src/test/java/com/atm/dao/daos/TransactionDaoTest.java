package com.atm.dao.daos;

import com.atm.controller.api.AccountsApi;
import com.atm.core.config.TestAuditingConfig;
import com.atm.model.dtos.payloads.records.responses.TransactionDto;
import com.atm.model.entities.Account;
import com.atm.model.entities.Transaction;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;


/**
 * Unit Testing DAO layer.
 */
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestAuditingConfig.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class TransactionDaoTest {

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private AccountDao accountDao;

    private Transaction transaction;
    private Account account;

    @BeforeEach
    void setUp() {
        // Saving parent entity
        Account ac = Account.builder()
                .number("number").balance(0).currency("$")
                .type("type").build();
        ac.setSlug("slug-account");
        // Arrange
        account = accountDao.save(ac);
        transaction = Transaction.builder()
                .amount(50.30).balanceAfter(100).receiptUrl("receipt_url")
                .account(account).build();
        transaction.setSlug("slug-transaction");
    }

    /**
     * Testing TransactionDao save() method
     */
    @Test
    void transactionDao_SaveTransaction() {
        // Act
        Transaction savedTransaction = transactionDao.save(transaction);
        // Assertion
        assertNotNull(savedTransaction.getId());
        assertEquals(transaction.getAccount().getId(), savedTransaction.getAccount().getId());
    }

    /**
     * Testing JPQL method of returning Account's transactions
     */
    @Test
    void transactionDao_FindAllByAccount() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        // Setting start and end dates for formatting results
        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);
        LocalDateTime endDate = LocalDateTime.now().plusMonths(1);
        // Act
        transactionDao.save(transaction);
        Page<TransactionDto> response = transactionDao
                .findAllByAccount(account.getId(),
                        startDate, endDate, pageable);
        // Assertion
        Assertions.assertThat(response.getTotalElements())
                .isEqualTo(1);
        Assertions.assertThat(response.getContent()
                        .get(0).getBalanceAfter())
                .isEqualTo(100);
    }

    /**
     * Testing JPQL method of returning Account's transactions
     */
    @Test
    void transactionDao_FindAllByAccount_ReturnsEmptyListWhenDateIsOutOfAvailableOnes () {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        // Setting start and end dates for formatting results
        LocalDateTime startDate = LocalDateTime.now().minusMonths(2);
        LocalDateTime endDate = LocalDateTime.now().minusMonths(1);
        // Act
        transactionDao.save(transaction);
        Page<TransactionDto> response = transactionDao
                .findAllByAccount(account.getId(),
                        startDate, endDate, pageable);
        // Assertion
        Assertions.assertThat(response.getTotalElements())
                .isEqualTo(0);
    }
}