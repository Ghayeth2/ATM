package com.atm.model.dtos.payloads.records.requests;

public record TransactionFilters(
        String email,
        String accountType,
        String accountNumber,
        String transactionType,
        String from,
        String to
) {
}
