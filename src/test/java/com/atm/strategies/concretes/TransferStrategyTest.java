package com.atm.strategies.concretes;

import com.atm.business.abstracts.AccountServices;
import com.atm.business.abstracts.ConfigService;
import com.atm.business.strategies.concretes.TransferStrategy;
import com.atm.core.exceptions.InsufficientFundsException;
import com.atm.model.dtos.TransactionContext;
import com.atm.model.entities.Account;
import com.atm.model.entities.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Testing TransferStrategy class.
 */
@ExtendWith(MockitoExtension.class)
class TransferStrategyTest {

    @Mock
    private AccountServices accountServices;

    @Mock
    private ConfigService configService;

    @Mock
    private Properties properties;

    @InjectMocks
    private TransferStrategy transferStrategy;


    /**
     * Testing execute method, where it should transfer funds between accounts
     * @throws IOException
     */
    @Test
    void transferStrategy_ExecuteMethod() throws IOException {
        // Setting up users for both accounts
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        // Account 1 & 2 will be used in mocking returned accounts for transfer
        // account1 will act as sender account
        Account account1 = Account.builder().number("number1")
                .type("Business Account")
                        .currency("$").balance(500).user(user1).build();
        // account2 will act as receiver account
        Account account2 = Account.builder().number("number2")
                .type("Business Account")
                        .currency("$").balance(300).user(user2).build();
        // Controlling the flow of outer services and responses / Mocking responses
        Mockito.when(configService.getProperties()).thenReturn(
                properties
        );
        Mockito.when(properties.getProperty(ArgumentMatchers.anyString()))
                .thenReturn("1");
        // Mocking returned sender account
        Mockito.when(accountServices.findByNumber(account1.getNumber()))
                .thenReturn(account1);
        // Mocking returned receiver account
        Mockito.when(accountServices.findByNumber(account2.getNumber()))
                .thenReturn(account2);
        // Mocking returned balanceAfter from withdraw method
        Mockito.when(accountServices.withdraw( ArgumentMatchers.anyString()
        , ArgumentMatchers.anyDouble()))
                .thenReturn(1500.00);
        // Mocking deposit method
        Mockito.when(accountServices.deposit(
                ArgumentMatchers.anyString(), ArgumentMatchers.anyDouble()
        ))
                .thenReturn(4.2);
        // Calling service method
        TransactionContext context = TransactionContext
                .builder().sender(account1.getNumber())
                .receiver(account2.getNumber())
                .amount(10).build();
        double balanceAfter = transferStrategy.execute(context);
        // Asserting the result
        assertEquals(1500.00, balanceAfter);
    }

    /**
     * Unit Testing for insufficient funds
     */
    @Test
    void transferStrategy_Execute_ShouldThrowException() throws IOException {
        // Transfer between user's accounts
        User user = new User();
        user.setId(1L);
        // Preparing accounts
        Account account1 = Account.builder().number("number1").type("Savings")
                .currency("$").balance(500).user(user).build();
        Account account2 = Account.builder().number("number2").type("Savings")
                .currency("$").balance(300).user(user).build();
        // Mocking responses
        Mockito.when(configService.getProperties()).thenReturn(properties);
        Mockito.when(properties.getProperty(ArgumentMatchers.anyString()))
                .thenReturn("1");
        // Receiver account
        Mockito.when(accountServices.findByNumber(account1.getNumber()))
                .thenReturn(account1);
        // Sender account
        Mockito.when(accountServices.findByNumber(account2.getNumber()))
                .thenReturn(account2);
        // Preparing the context
        TransactionContext context = TransactionContext.builder()
                .amount(800).receiver(account1.getNumber())
                .sender(account2.getNumber()).build();
        // Assertion for exception
        InsufficientFundsException exception = Assertions.assertThrows(
                InsufficientFundsException.class,
                () -> transferStrategy.execute(context)
        );
        Assertions.assertEquals("Insufficient funds", exception.getMessage());

    }

}