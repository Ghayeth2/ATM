package com.atm.strategies.concretes;

import com.atm.business.abstracts.AccountServices;
import com.atm.business.strategies.concretes.DepositStrategy;
import com.atm.core.exceptions.InsufficientFundsException;
import com.atm.model.dtos.TransactionContext;
import com.atm.model.entities.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit testing DepositStrategy
 */
@ExtendWith(MockitoExtension.class)
class DepositStrategyTest {

    @Mock
    private AccountServices accountServices;

    @InjectMocks
    private DepositStrategy depositStrategy;

    private Account account;
    private TransactionContext context;

    @BeforeEach
    void setUp() {
        // setting data used in test
        account = Account.builder().number("number")
                .type("type").balance(500).build();
        context = TransactionContext.builder().receiver(
                account.getNumber()
        ).build();
    }

    /**
     * Testing valid funds and depositing to account.
     */
    @Test
    void depositStrategy_ExecuteDepositing() {
        // Setting amount to be deposited
        context.setAmount(50.567);
        // Mocking services
        Mockito.when(accountServices.deposit(Mockito.anyString(),
                Mockito.anyDouble()))
                .thenReturn(550.60);
        // Act
        double res = depositStrategy.execute(context);
        // Asserting result
        assertEquals(550.60, res);
    }

    /**
     * Testing insufficient funds to deposit
     */
    @Test
    void depositStrategy_ExecuteMethod_ThrowsException() {
        // Setting amount
        context.setAmount(0);
        // Asserting throwing exception
        InsufficientFundsException ex = assertThrows(
                InsufficientFundsException.class,
                () -> depositStrategy.execute(context)
        );
        assertEquals("Insufficient funds!", ex.getMessage());
    }

}