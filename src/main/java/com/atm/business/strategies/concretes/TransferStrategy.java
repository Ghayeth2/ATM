package com.atm.business.strategies.concretes;

import com.atm.business.abstracts.AccountServices;
import com.atm.business.abstracts.ConfigService;
import com.atm.core.exceptions.InsufficientFundsException;
import com.atm.model.dtos.TransactionContext;
import com.atm.model.entities.Account;
import com.atm.business.strategies.abstracts.TransactionsStrategy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
    public double execute(TransactionContext context) {
        System.out.println("Data sent to during Unit Test:"
                + context.getAmount() + " " + context.getSender());
        // Retrieving fees values from config file
        double businessFees = Double.parseDouble(configService.getProperties()
                .getProperty("fees.business"));
        double savingsFees = Double.parseDouble(configService.getProperties()
                .getProperty("fees.savings"));
        double personalFees = Double.parseDouble(configService.getProperties()
                .getProperty("fees.personal"));
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
        var balanceAfter = 0.0;
        // Insufficient funds // if not same user
        if (!isSameUserAccounts(sender, receiver)) {
            if (sender.getType().contains("Business")
                    && sender.getBalance() < totalBusiness)
                throw new InsufficientFundsException("Insufficient funds");
            else if (sender.getType().contains("Personal")
                    && sender.getBalance() < totalPersonal)
                throw new InsufficientFundsException("Insufficient funds");
            else if (sender.getType().contains("Savings")
                    && sender.getBalance() < totalSavings)
                throw new InsufficientFundsException("Insufficient funds");
        } // If same user
        else {
            if (sender.getBalance() < context.getAmount())
                throw new InsufficientFundsException("Insufficient funds");
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

    private double transferFunds(double withdrawnAmount,
                                 double receivedAmount, String... accounts) {
        double balanceAfter = 0.0;
//        System.out.println(accounts.length);
        // Withdrawing amount from sender's account + fee
        balanceAfter = accountServices.withdraw(
                accounts[0],
                withdrawnAmount
        );
        // Depositing funds into receiver's account
        accountServices.deposit(
                accounts[1],
                receivedAmount
        );
        // Formatting the result to .2f
        balanceAfter = Math.round(balanceAfter * 100) / 100.0;
        return balanceAfter;
    }
}
