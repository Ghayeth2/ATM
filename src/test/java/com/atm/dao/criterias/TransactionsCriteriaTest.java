package com.atm.dao.criterias;

import com.atm.core.config.TestAuditingConfig;
import com.atm.core.utils.converter.DateFormatConverter;
import com.atm.dao.daos.AccountDao;
import com.atm.dao.daos.TransactionDao;
import com.atm.dao.daos.UserDao;
import com.atm.model.dtos.payloads.records.requests.TransactionsCriteriaRequest;
import com.atm.model.dtos.payloads.records.responses.UserAccountTransaction;
import com.atm.model.entities.Account;
import com.atm.model.entities.Transaction;
import com.atm.model.entities.User;
import org.junit.jupiter.api.Assertions;
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
 * Unit testing TransactionsCriteria API's repository.
 */
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
@Import({
        TestAuditingConfig.class,
        TransactionsCriteria.class
})
class TransactionsCriteriaTest {
    // Setting required dependencies
    @Autowired
    private UserDao userDao;
    @Autowired
    private TransactionDao transactionDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    TransactionsCriteria criteria;
    // Setting test vars
    private User user1;
    private User user2;
    private List<Account> accounts = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();

    // Setting up the data in H2 before each test
    @BeforeEach
    void setUp() {
        // Setting up two users for accounts lists
        user1 = User.builder()
                .email("ghayeth@gmail.com").firstName("Ghayeth")
                .lastName("almasri").password("pass")
                .accountNonLocked(1).failedAttempts(0)
                .build();
        user1.setSlug("user-slug");
        // 2nd user
        user2 = User.builder()
                .email("muhammad@gmail.com").firstName("muhammad")
                .lastName("almasri").password("pass")
                .accountNonLocked(1).failedAttempts(0)
                .build();
        user2.setSlug("user2-slug");
        // Save users
        userDao.saveAll(Arrays.asList(user1, user2));

        // Setting up user1 accounts and saving them
        Account account = Account.builder().number("1000-2020-2323-1001")
                .currency("!").type("Personal Account").user(user1).build();
        account.setSlug("user1-account-slug");
        Account account1 = Account.builder().number("1000-3242-2034-1002")
                .currency("#").type("Business Account").user(user1).build();
        account1.setSlug("user1-account1-slug");
        Account account2 = Account.builder().number("1000-4788-9099-1003")
                .currency("&").type("Savings Account").user(user1).build();
        account2.setSlug("user1-account2-slug");
        accounts.addAll(Arrays.asList(account, account1, account2));
        accountDao.saveAll(accounts);

        // Retrieve managed accounts from the database
        Account managedAccount = accountDao.findBySlug("user1-account-slug").get();
        Account managedAccount1 = accountDao.findBySlug("user1-account1-slug").get();
        Account managedAccount2 = accountDao.findBySlug("user1-account2-slug").get();

        // Initializing Transaction models & saving them
        Transaction transaction = Transaction.builder().receiptUrl("receipt-url")
                .account(managedAccount).amount(400).type("Transfer").build();
        transaction.setSlug("user1-account-transaction-slug");
        Transaction transaction1 = Transaction.builder().amount(250)
                .receiptUrl("receipt_url").account(managedAccount1).type("Transfer").build();
        transaction1.setSlug("user1-account1-transaction1-slug");
        Transaction transaction2 = Transaction.builder().amount(700).type("Deposit")
                .account(managedAccount2).receiptUrl("receipt_urll").build();
        transaction2.setSlug("user1-account2-transaction2-slug");
        transactions.addAll(Arrays.asList(transaction, transaction1, transaction2));
        transactionDao.saveAll(transactions);

        // Clear lists
        accounts.clear();
        transactions.clear();

        // Repeat for user2 accounts and transactions
        account = Account.builder().number("1000-2020-2323-1004")
                .currency("!").type("Personal Account").user(user2).build();
        account.setSlug("user2-account-slug");
        account1 = Account.builder().number("1000-2020-2323-1005")
                .currency("!").type("Personal Account").user(user2).build();
        account1.setSlug("user2-account1-slug");
        account2 = Account.builder().number("1000-2020-2323-1006")
                .currency("!").type("Personal Account").user(user2).build();
        account2.setSlug("user2-account2-slug");
        accounts.addAll(Arrays.asList(account, account1, account2));
        accountDao.saveAll(accounts);

        managedAccount = accountDao.findBySlug("user2-account-slug").get();
        managedAccount1 = accountDao.findBySlug("user2-account1-slug").get();
        managedAccount2 = accountDao.findBySlug("user2-account2-slug").get();

        transaction = Transaction.builder().receiptUrl("receipt-url_ke")
                .account(managedAccount).amount(200).type("Withdrawal").build();
        transaction.setSlug("user2-account-transaction-slug");
        transaction1 = Transaction.builder().amount(70)
                .receiptUrl("receipt_url78").account(managedAccount1).type("Deposit").build();
        transaction1.setSlug("user2-account1-transaction1-slug");
        transaction2 = Transaction.builder().amount(650).type("Deposit")
                .account(managedAccount2).receiptUrl("receipt_url4l").build();
        transaction2.setSlug("user2-account2-transaction2-slug");
        transactions.addAll(Arrays.asList(transaction, transaction1, transaction2));
        transactionDao.saveAll(transactions);

        accounts.clear();
        transactions.clear();
    }


    /**
     * Retrieving all data without any filter.
     */
    @Test
    void transactionCriteria_FindAllDataNoFilters() {
        // preparing mandatory request's data
        LocalDateTime startDate = new DateFormatConverter().formatRequestDate(
                LocalDateTime.now().minusMonths(1)
        );
        LocalDateTime endDate = new DateFormatConverter().formatRequestDate(
                LocalDateTime.now()
        );
        Pageable pageable = PageRequest.of(0, 5);
        TransactionsCriteriaRequest request =
                new TransactionsCriteriaRequest(
                        "", "createdDate", "desc", startDate,
                        endDate, pageable, 0.0, 0.0
                );
        // Sending request
        Page<UserAccountTransaction> response = criteria.findAll(request);
        // Asserting results
        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getTotalPages(), 2);
        Assertions.assertEquals(6, response.getTotalElements());
    }

    /**
     * Filter: firstName
     */
    @Test
    void transactionCriteria_FindAllFilteredFirstName() {
        // preparing mandatory request's data
        LocalDateTime startDate = new DateFormatConverter().formatRequestDate(
                LocalDateTime.now().minusMonths(1)
        );
        LocalDateTime endDate = new DateFormatConverter().formatRequestDate(
                LocalDateTime.now()
        );
        Pageable pageable = PageRequest.of(0, 5);
        TransactionsCriteriaRequest request =
                new TransactionsCriteriaRequest(
                        "ghayeth", "createdDate", "desc", startDate,
                        endDate, pageable, 0.0, 0.0
                );
        // Calling the service
        Page<UserAccountTransaction> response = criteria.findAll(request);
        // Assertions
        Assertions.assertEquals(3, response.getTotalElements());
        Assertions.assertEquals("Ghayeth almasri",
                response.getContent().get(0).getFullName());
    }

    /**
     * Filter: transaction type
     */
    @Test
    void transactionCriteria_FindAllFilteredTransactionType() {
        // preparing mandatory request's data
        LocalDateTime startDate = new DateFormatConverter().formatRequestDate(
                LocalDateTime.now().minusMonths(1)
        );
        LocalDateTime endDate = new DateFormatConverter().formatRequestDate(
                LocalDateTime.now()
        );
        Pageable pageable = PageRequest.of(0, 5);
        TransactionsCriteriaRequest request =
                new TransactionsCriteriaRequest(
                        "Deposit", "createdDate", "desc", startDate,
                        endDate, pageable, 0.0, 0.0
                );
        // Calling the service
        Page<UserAccountTransaction> response = criteria.findAll(request);
        // Asserting result
        Assertions.assertEquals(response.getContent().size(), 3);
    }

    /**
     * Filter: account type
     */
    @Test
    void transactionCriteria_FindAllFilteredAccountType() {
        // preparing mandatory request's data
        LocalDateTime startDate = new DateFormatConverter().formatRequestDate(
                LocalDateTime.now().minusMonths(1)
        );
        LocalDateTime endDate = new DateFormatConverter().formatRequestDate(
                LocalDateTime.now()
        );
        Pageable pageable = PageRequest.of(0, 5);
        TransactionsCriteriaRequest request =
                new TransactionsCriteriaRequest(
                        "pers", "createdDate", "desc", startDate,
                        endDate, pageable, 0.0, 0.0
                );
        // Calling the service
        Page<UserAccountTransaction> response = criteria.findAll(request);
        // Asserting result
        Assertions.assertEquals(4, response.getTotalElements());
    }

    /**
     * Filters: lastName & amount rage
     */
    @Test
    void transactionCriteria_FindAllFilteredLastNameAndAmount() {
        // preparing mandatory request's data
        LocalDateTime startDate = new DateFormatConverter().formatRequestDate(
                LocalDateTime.now().minusMonths(1)
        );
        LocalDateTime endDate = new DateFormatConverter().formatRequestDate(
                LocalDateTime.now()
        );
        Pageable pageable = PageRequest.of(0, 5);
        TransactionsCriteriaRequest request =
                new TransactionsCriteriaRequest(
                        "almasri", "createdDate", "desc", startDate,
                        endDate, pageable, 200.0, 500.0
                );
        // Calling the service
        Page<UserAccountTransaction> response = criteria.findAll(request);
        // Asserting result
        Assertions.assertEquals(3, response.getTotalElements());
    }

    /**
     * Filters (sort): by amount
     */
    @Test
    void transactionCriteria_FindAllFilteredSortByAmount() {
        // preparing mandatory request's data
        LocalDateTime startDate = new DateFormatConverter().formatRequestDate(
                LocalDateTime.now().minusMonths(1)
        );
        LocalDateTime endDate = new DateFormatConverter().formatRequestDate(
                LocalDateTime.now()
        );
        Pageable pageable = PageRequest.of(0, 5);
        TransactionsCriteriaRequest request =
                new TransactionsCriteriaRequest(
                        "", "amount", "desc", startDate,
                        endDate, pageable, 0.0, 0.0
                );
        // Calling the service
        Page<UserAccountTransaction> response = criteria.findAll(request);
        // Asserting results
        Assertions.assertEquals("Deposit",
                response.getContent().get(0).getTransactionType());
        Assertions.assertEquals("Ghayeth almasri",
                response.getContent().get(0).getFullName());
    }

    /**
     * Filters: date (not existed one)
     */
    @Test
    void transactionCriteria_FindAllFilteredByDate() {
        // preparing mandatory request's data
        LocalDateTime startDate = new DateFormatConverter().formatRequestDate(
                LocalDateTime.now().minusMonths(1)
        );
        LocalDateTime endDate = new DateFormatConverter().formatRequestDate(
                LocalDateTime.now().minusDays(15)
        );
        Pageable pageable = PageRequest.of(0, 5);
        TransactionsCriteriaRequest request =
                new TransactionsCriteriaRequest(
                        "", "createdDate", "desc", startDate,
                        endDate, pageable, 0.0, 0.0
                );
        // Calling the service
        Page<UserAccountTransaction> response = criteria.findAll(request);
        Assertions.assertEquals(0, response.getTotalElements());
    }

    /**
     * Filters: transaction type, amount range sort by amount
     */
    @Test
    void transactionCriteria_FindAllFilteredMultiple() {
        // preparing mandatory request's data
        LocalDateTime startDate = new DateFormatConverter().formatRequestDate(
                LocalDateTime.now().minusMonths(1)
        );
        LocalDateTime endDate = new DateFormatConverter().formatRequestDate(
                LocalDateTime.now()
        );
        Pageable pageable = PageRequest.of(0, 5);
        TransactionsCriteriaRequest test1 =
                new TransactionsCriteriaRequest(
                        "Transfer", "amount", "asc", startDate,
                        endDate, pageable, 50, 500
                );
        TransactionsCriteriaRequest test2 =
                new TransactionsCriteriaRequest(
                        "Transfer", "amount", "asc", startDate,
                        endDate, pageable, 259, 500
                );
        // Calling the service
        Page<UserAccountTransaction> response = criteria.findAll(test1);
        Page<UserAccountTransaction> response1 = criteria.findAll(test2);
        // Asserting results
        Assertions.assertEquals(2, response.getTotalElements());
        Assertions.assertEquals(250.0,
                response.getContent().get(0).getAmount());
        Assertions.assertEquals(1, response1.getTotalElements());
        Assertions.assertEquals(400,
                response1.getContent().get(0).getAmount());
    }
}
