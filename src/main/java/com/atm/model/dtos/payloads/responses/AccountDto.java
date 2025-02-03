package com.atm.model.dtos.payloads.responses;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Builder @Getter @Setter
@AllArgsConstructor @NoArgsConstructor
// user details will be fetched from auth object. Logged in user
public class AccountDto {
    private String slug;
    private String currency;
    private String number;
    private double balance;
    private String type;
    private LocalDateTime createdDate;
    private String formattedDate;

    /**
     * This constructor is used when the Criteria API
     * is executing.
     * @param slug
     * @param currency
     * @param number
     * @param balance
     * @param type
     * @param createdDate
     */
    public AccountDto(String slug,
                      String currency,
                      String number,
                      double balance,
                      String type,
                      LocalDateTime createdDate) {
        this.slug = slug;
        this.currency = currency;
        this.number = number;
        this.balance = balance;
        this.type = type;
        this.createdDate = createdDate;
    }
}
