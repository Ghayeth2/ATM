package com.atm.model.dtos.payloads.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/*
    Search params:
    fullName (first or last),
    email, transaction type, account type.

    Sort:
    date, email, amount.

    Criteria API is Required!
 */
/*
    Default data:
    - All users, accounts, and transactions.
    Filtered:
    - user's (first name, last name, email
    , account type, transaction type)
    - sorting by: date, amount, email, lastName, firstName
    , accountType, transactionType, balanceAfter.
 */
/**
 * The data object will be used for returning
 * all users' transactions along with all details.
 */
@Builder @Getter @AllArgsConstructor
public class UserAccountTransaction {
    private String fullName;
    private String email;
    private String accountType;
    private String transactionType;
    private String formattedDate;
    private LocalDateTime date;
    private double amount;
    private double balanceAfter;

    public UserAccountTransaction(double balanceAfter,
                                  double amount,
                                  LocalDateTime date,
                                  String transactionType,
                                  String accountType,
                                  String email,
                                  String fullName) {
        this.balanceAfter = balanceAfter;
        this.amount = amount;
        this.date = date;
        this.transactionType = transactionType;
        this.accountType = accountType;
        this.email = email;
        this.fullName = fullName;
    }
}
