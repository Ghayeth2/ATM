package com.atm.business.concretes;

import com.atm.business.abstracts.AccountServices;
import com.atm.business.abstracts.ConfigService;
import com.atm.core.exceptions.AccountsCurrenciesMismatchException;
import com.atm.dao.criterias.TransactionsCriteria;
import com.atm.dao.daos.TransactionDao;
import com.atm.model.dtos.payloads.records.requests.TransactionsCriteriaRequest;
import com.atm.model.dtos.payloads.records.requests.TransactionsFiltersRequest;
import com.atm.model.dtos.payloads.responses.TransactionDto;
import com.atm.model.dtos.payloads.responses.UserAccountTransaction;
import com.atm.model.entities.Account;
import com.atm.model.entities.Transaction;
import com.atm.model.entities.User;
import com.atm.business.strategies.concretes.WithdrawalStrategy;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

@ExtendWith(MockitoExtension.class)
class TransactionManagerTest {

    @Mock
    private WithdrawalStrategy strategy;

    @Mock
    private TransactionsCriteria criteria;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private AccountServices accountServices;

    @Mock
    private ConfigService configService;

    @Mock
    private Properties properties;

    @Mock
    private TransactionDao transactionDao;

    @InjectMocks
    private TransactionManager transactionManager;

    /**
     * Unit testing Service Layer's findAllFiltered method
     */
    @Test @SneakyThrows
    void transactionManager_FindAllFiltered() {
        // Mock list
        List<UserAccountTransaction> list = List.of(
                UserAccountTransaction.builder()
                .date(LocalDateTime.now())
                .email("email").accountType("accountType")
                .fullName("ghayeth al masri").build());
        Page<UserAccountTransaction> page = new PageImpl<>(list);
        // Mock services
        Mockito.when(configService.getProperties())
                .thenReturn(properties);
        Mockito.when(properties.getProperty(Mockito.anyString()))
                .thenReturn("5");
        Mockito.when(criteria.findAll(Mockito.any(
                TransactionsCriteriaRequest.class
        ))).thenReturn(page);
        // Calling service & Asserting expected result
        Page<UserAccountTransaction> response =
                transactionManager.findAllFiltered(
                        new TransactionsFiltersRequest(
                                "","",
                                "", ""
                                ,"", 0.0
                                ,0.0, 1
                        )
                );
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getTotalElements());
    }

    /**
     * Testing findAllByAccount service.
     */
    @Test @SneakyThrows
    void transactionManager_FindAllByAccount() {
        // Mock data
        Account account = Account.builder().number("1000-3245-4314-1001")
                .type("Business").currency("$")
                .balance(800.0).build();
        account.setId(1L);
        Page<TransactionDto> data = new PageImpl<> (List.of(
                TransactionDto.builder().createdDate(
                        LocalDateTime.now()
                ).amount(500).type("Transfer").build()
        ));
        Pageable pageable = PageRequest.of(1, 5);
        // Mocking services
        Mockito.when(accountServices.findBySlug(Mockito.anyString()))
                .thenReturn(account);
        Mockito.when(configService.getProperties())
                .thenReturn(properties);
        Mockito.when(properties.getProperty(Mockito.anyString()))
                .thenReturn("5");
        Mockito.when(transactionDao.findAllByAccount(
                Mockito.anyLong(), Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class)
        )).thenReturn(data);
        // Calling the service and asserting result
        Page<TransactionDto> response =
                transactionManager.findAllByAccount(
                        "slug","",""
                        ,1, "",""
                );
        Assertions.assertNotNull(response);
        Assertions.assertEquals("Transfer",
                response.getContent().get(0).getType());
    }

    /**
     * Withdrawal transaction tested (calling, creating receipt)
     * from TransactionManager class.
     * @throws IOException
     */
    @Test
    void transactionManager_NewTransaction() throws IOException {
        // Preparing data will be returned as result for Mocks
        Transaction transaction = Transaction.builder()
                .balanceAfter(500).receiptUrl("receipt_mocked.html")
                .build();
        User user = User.builder().firstName("first").lastName("last")
                .build();
        Account account = Account.builder().number("1000-3245-4314-1001")
                        .type("Business").user(user).currency("$")
                        .balance(800.0).build();
        // Preparing Mocks for each external service
        double[] balance = {400};
        Mockito.when(strategy.execute(Mockito.any()))
                .thenReturn(balance);
        Mockito.when(accountServices.findByNumber(Mockito.anyString()))
                .thenReturn(account);
        Mockito.when(configService.getProperties())
                .thenReturn(properties);
        Mockito.when(properties.getProperty("transactions.receipts.path"))
                .thenReturn("D:\\\\files\\\\receipts");
        Mockito.when(transactionDao.save(Mockito.any()))
                .thenReturn(transaction);
        // Calling the service class
        String[] numbers = {"", account.getNumber()};
        String res = transactionManager.newTransaction(
                "Withdrawal", "40", numbers
        );
        // Asserting response
        Assertions.assertEquals(
                "Transaction is saved: "+transaction.getType(),
                res
        );
    }

    /**
     * Testing if accounts' currencies are different, it should throw exception.
     */
    @Test
    void transactionManager_NewTransaction_ShouldThrowExceptionWhenCurrenciesMismatchInTransfer() {
        // Preparing mocked accounts
        Account account1 = Account.builder().type("Business")
                .currency("$").number("number1").build();
        Account account2 = Account.builder().type("Business")
                .currency("tl").number("number2").build();
        // Mocking services
        Mockito.when(accountServices.findByNumber(
                account1.getNumber()
        )).thenReturn(account1);
        Mockito.when(accountServices.findByNumber(
                account2.getNumber()
        )).thenReturn(account2);
        // Acting & asserting exception is thrown
        String[] numbers = {account1.getNumber(), account2.getNumber()};
        AccountsCurrenciesMismatchException ex =
                Assertions.assertThrows(
                        AccountsCurrenciesMismatchException.class,
                        () -> transactionManager
                                .newTransaction("Transfer"
                                ,"50", numbers)
                );
        Assertions.assertEquals(ex.getMessage(),
                "Currency of receiver account does " +
                        "not match currencies of transaction"
                );
    }

    /**
     * Transfer strategy, testing creating receipt, and do the transfer.
     */
    @Test
    void transactionManager_NewTransaction_Transfer() throws IOException {
        // Mocked transaction model
        Transaction transaction = Transaction.builder()
                .type("Transfer").amount(300).receiptUrl("url")
                .build();
        // Users for each account
        User user = User.builder().firstName("Ghayeth")
        .lastName("Al Masri").build();
        user.setId(1L);
        User user1 = User.builder().firstName("Ahmad")
        .lastName("Al Sharaa").build();
        user1.setId(2L);
        // Accounts initializing
        // Sender
        Account account = Account.builder().balance(500).number(
                "1111-2323-5555-1013"
        ).currency("TL").type("Savings").user(user).build();
        // Receiver
        Account account1 = Account.builder().number(
                "4344-3774-1345-8473"
        ).currency("TL").type("Business").user(user1).build();
        // Numbers for request
        String[] numbers = {account.getNumber(), account1.getNumber()};
        // Mocking services
        Mockito.when(accountServices.findByNumber(
                account.getNumber()
        )).thenReturn(account);
        Mockito.when(accountServices.findByNumber(
                account1.getNumber()
        )).thenReturn(account1);
        Mockito.when(configService.getProperties())
                .thenReturn(properties);
        Mockito.when(properties.getProperty("transactions.fees.savings"))
                .thenReturn("0.01");
        Mockito.when(properties.getProperty("transactions.receipts.path"))
                .thenReturn("D:\\\\files\\\\receipts");
        Mockito.when(transactionDao.save(Mockito.any(Transaction.class)))
                .thenReturn(transaction);
        // Calling the service
        String response = transactionManager.newTransaction(transaction.getType(),
                "300", numbers);
        // Asserting result
        Assertions.assertEquals(response, "Transaction is saved: Transfer");

    }
}