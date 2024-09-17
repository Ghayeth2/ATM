package com.atm.model.dtos;

import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.extern.log4j.Log4j2;

@Builder @Getter @Setter
@AllArgsConstructor @NoArgsConstructor
// user details will be fetched from auth object. Logged in user
public class AccountDto {
    private String slug;
    private String number;
    private double balance;
    private AccountTypes type;
}
