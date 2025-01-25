package com.atm.model.dtos.payloads.records.responses;
/*
    Search params:
    fullName (first or last),
    email, transaction type, account type.

    Sort:
    date, email, amount.

    Criteria API is Required!
 */
public record UserAccountTransaction(
        String date,
        String fullName,
        String email,
        String accountType,
        String transactionType,
        String description,
        double amount,
        double currentBalance
) {
}
