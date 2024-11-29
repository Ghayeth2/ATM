package com.atm.model.enums;

import lombok.Getter;

@Getter
public enum Currencies {
    USD("US Dollar", "$"),
    EUR("Euro", "€"),
    GBP("British Pound", "£"),
    TRY("Turkish Lira", "₺");

    private final String name;
    private final String symbol;

    Currencies(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }
}
