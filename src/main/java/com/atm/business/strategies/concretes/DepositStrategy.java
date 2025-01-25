package com.atm.business.strategies.concretes;

import com.atm.business.abstracts.AccountServices;
import com.atm.core.exceptions.InsufficientFundsException;
import com.atm.model.dtos.TransactionContext;
import com.atm.business.strategies.abstracts.TransactionsStrategy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component("depositStrategy")
@RequiredArgsConstructor
@Service
public class DepositStrategy implements TransactionsStrategy {

    private final AccountServices accountServices;

    /**
     * Validating amount's value if is valid, calling the actual
     * service and depositing funds.
     * @return
     */
    @Override @SneakyThrows
    public double execute(TransactionContext context) {
        double balanceAfter;
        if (context.getAmount() > 0) {
            balanceAfter = accountServices.deposit(context.getReceiver(), context.getAmount());
        } else {
            throw new InsufficientFundsException(
                    "Insufficient funds!"
            );
        }
        // Formatting the result to .2f
        balanceAfter = Math.round(balanceAfter * 100) / 100.0;
        return balanceAfter;
    }
}
