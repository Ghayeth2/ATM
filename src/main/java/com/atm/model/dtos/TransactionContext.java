package com.atm.model.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder @Getter @Setter
public class TransactionContext {
    private double amount;
    private String receiver;
    private String sender;
}
