package com.atm.model.enums;

import lombok.Getter;


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
