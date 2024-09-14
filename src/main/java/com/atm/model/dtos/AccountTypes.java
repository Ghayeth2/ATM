package com.atm.model.dtos;

import lombok.Getter;
import lombok.Setter;


@Getter
public enum AccountTypes {
    BUSINESS("Business Account"),
    PERSONAL("Personal Account"),
    SAVINGS("Savings Account");

    private final String content;

    AccountTypes(String content) {
        this.content = content;
    }

}
