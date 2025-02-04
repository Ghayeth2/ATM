package com.atm.business.strategies.concretes;

import com.atm.business.abstracts.AccountServices;
import com.atm.business.abstracts.ConfigService;
import com.atm.core.exceptions.InsufficientFundsExceptionTransfer;
import com.atm.core.exceptions.InsufficientFundsExceptionWithdraw;
import com.atm.model.dtos.TransactionContext;
import com.atm.model.entities.Account;
import com.atm.business.strategies.abstracts.TransactionsStrategy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Component("transferStrategy")
@RequiredArgsConstructor
@Service
public class TransferStrategy implements TransactionsStrategy {

    private final AccountServices accountServices;
    private final ConfigService configService;

    /**
     * If the balance is valid from sender account,
     * transfer will get executed.
     *
     * @param context
     * @return
     */
    @Override
    @SneakyThrows
    public double[] execute(TransactionContext context) {
        System.out.println("Data sent to during Unit Test:"
                + context.getAmount() + " " + context.getSender());
        // Retrieving fees values from config file
        double businessFees = Double.parseDouble(configService.getProperties()
                .getProperty("transactions.fees.business"));
        double savingsFees = Double.parseDouble(configService.getProperties()
                .getProperty("transactions.fees.savings"));
        double personalFees = Double.parseDouble(configService.getProperties()
                .getProperty("transactions.fees.personal"));
        System.out.println("Business Fees: " + businessFees);
        // Retrieving accounts (sender & receiver) for transfer
        Account receiver = accountServices.findByNumber(context.getReceiver());
        Account sender = accountServices.findByNumber(context.getSender());
//        System.out.println("Sender details: " + sender.getNumber()
//                + " " + sender.getType());
        // Calculating total amount will be withdrawn from sender account for 3 types
        double totalBusiness = context.getAmount() +
                (context.getAmount() * businessFees);
        double totalSavings = context.getAmount() +
                (context.getAmount() * savingsFees);
        double totalPersonal = context.getAmount() +
                (context.getAmount() * personalFees);
        // numbers
        String[] numbers = {sender.getNumber(), receiver.getNumber()};
//        System.out.println(numbers.length);
        // Balance after transfer is executed for sender account
        double[] balanceAfter = new double[2];
        // Insufficient funds // if not same user
        if (!isSameUserAccounts(sender, receiver)) {
            if (sender.getType().contains("Business")
                    && sender.getBalance() < totalBusiness)
                throw new InsufficientFundsExceptionTransfer("Insufficient funds");
            else if (sender.getType().contains("Personal")
                    && sender.getBalance() < totalPersonal)
                throw new InsufficientFundsExceptionTransfer("Insufficient funds");
            else if (sender.getType().contains("Savings")
                    && sender.getBalance() < totalSavings)
                throw new InsufficientFundsExceptionTransfer("Insufficient funds");
        } // If same user
        else {
            if (sender.getBalance() < context.getAmount())
                throw new InsufficientFundsExceptionTransfer("Insufficient funds");
        }
        // Insufficient funds exception ending
        // Is it transfer between user's accounts??
        if (!isSameUserAccounts(sender, receiver)) {
            // Transfer from different users' accounts
            if (sender.getType().contains("Business"))
                balanceAfter = transferFunds(totalBusiness, context.getAmount(),
                        numbers);
            else if (sender.getType().contains("Personal"))
                balanceAfter = transferFunds(totalPersonal, context.getAmount(), numbers);
            else if (sender.getType().contains("Savings"))
                balanceAfter = transferFunds(totalSavings, context.getAmount(), numbers);
        } else
            // Transfer between user's accounts
            balanceAfter = transferFunds(context.getAmount(), context.getAmount(),
                    numbers);
        return balanceAfter;
    }

    private boolean isSameUserAccounts(Account sender, Account receiver) {
        return Objects.equals(sender.getUser().getId(), receiver.getUser().getId());
    }

    private double[] transferFunds(double withdrawnAmount,
                                 double receivedAmount, String... accounts) {
        double balanceAfterWithdraw = 0.0;
        double balanceAfterDeposit = 0.0;
//        System.out.println(accounts.length);
        // Withdrawing amount from sender's account + fee
        balanceAfterWithdraw = accountServices.withdraw(
                accounts[0],
                withdrawnAmount
        );
        // Depositing funds into receiver's account
        balanceAfterDeposit = accountServices.deposit(
                accounts[1],
                receivedAmount
        );
        // Formatting the result to .2f
        balanceAfterWithdraw = Math.round(balanceAfterWithdraw * 100) / 100.0;
        balanceAfterDeposit = Math.round(balanceAfterDeposit * 100) / 100.0;

        double[] res = {balanceAfterWithdraw, balanceAfterDeposit};
        log.info("TransferStrategy -> execute -> double[] length: " + res.length);
        return res;
    }
}
