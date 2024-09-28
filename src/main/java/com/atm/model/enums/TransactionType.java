package com.atm.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionType {
    WITHDRAWAL("Withdrawal money"),
    DEPOSIT("Deposit money"),
    TRANSFER("Transfer money");

    private final String transaction;
}
