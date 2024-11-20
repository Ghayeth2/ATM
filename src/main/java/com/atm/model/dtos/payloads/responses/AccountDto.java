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

}
