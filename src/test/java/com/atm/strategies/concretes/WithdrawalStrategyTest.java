package com.atm.strategies.concretes;

import com.atm.business.abstracts.AccountServices;
import com.atm.business.strategies.concretes.WithdrawalStrategy;
import com.atm.core.exceptions.InsufficientFundsExceptionWithdraw;
import com.atm.model.dtos.TransactionContext;
import com.atm.model.entities.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Testing WithdrawalStrategy service.
 */
@ExtendWith(MockitoExtension.class)
class WithdrawalStrategyTest {

    @Mock
    private AccountServices accountServices;

    @InjectMocks
    private WithdrawalStrategy withdrawalStrategy;

    private Account account;
    private TransactionContext context;
    @BeforeEach
    void setUp() {
        account = Account.builder().number("number")
                .type("type").currency("currency")
                .balance(4400).build();
        context = TransactionContext.builder()
                .receiver(account.getNumber())
                .build();
    }

    /**
     * Sufficient funds.
     */
    @Test
    void withdrawalStrategy_ExecuteWithdrawalStrategy() {
        // Mocking responses
        Mockito.when(accountServices.findByNumber(
                Mockito.anyString()
        )).thenReturn(account);
        Mockito.when(accountServices.withdraw(
                Mockito.anyString(),
                Mockito.anyDouble()
        )).thenReturn(4000.0);
        context.setAmount(400);
        // Act
        double response = withdrawalStrategy.execute(context);
        // Asserting result
        assertEquals(response, 4000.00);
    }

    /**
     * Insufficient funds
     */
    @Test
    void withdrawalStrategy_ExecuteWithoutBalance() {
        // Mocking responses
        Mockito.when(accountServices.findByNumber(
                Mockito.anyString()
        )).thenReturn(account);
        // Setting the number for context
        context.setAmount(10000.00);
        // Asserting throwing exception
        InsufficientFundsExceptionWithdraw ex = Assertions.assertThrows(
                InsufficientFundsExceptionWithdraw.class,
                () -> withdrawalStrategy.execute(context)
        );
        assertEquals(ex.getMessage(), "Insufficient funds!");
    }
}