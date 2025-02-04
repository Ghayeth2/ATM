package com.atm.business.strategies.concretes;

import com.atm.business.abstracts.AccountServices;
import com.atm.core.exceptions.InsufficientFundsExceptionWithdraw;
import com.atm.model.dtos.TransactionContext;
import com.atm.model.entities.Account;
import com.atm.business.strategies.abstracts.TransactionsStrategy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Component("withdrawalStrategy")
@RequiredArgsConstructor
@Service
public class WithdrawalStrategy implements TransactionsStrategy {

    private final AccountServices accountServices;

    /**
     * Validating account's current balance & amount to withdraw,
     * then calling the actual withdrawing service.
     * @return
     */
    @Override @SneakyThrows
    public double[] execute(TransactionContext context) {
        // Retrieving account
        Account account = accountServices.findByNumber(context.getReceiver());
        // Checking amount to withdraw & account's balance
        double balanceAfter = 0;
        if (account != null) {
            if (account.getBalance() >= context.getAmount()) {
                balanceAfter = accountServices.withdraw(context.getReceiver()
                        , context.getAmount());
            } else {
                throw new InsufficientFundsExceptionWithdraw(
                        "Insufficient funds!"
                );
            }
        }
        // Formatting the result to .2f
        balanceAfter = Math.round(balanceAfter * 100) / 100.0;
        double[] res = {balanceAfter};
        log.info("WithdrawalStrategy -> execute -> returned double[] length: " + res.length);
        return res;
    }
}
