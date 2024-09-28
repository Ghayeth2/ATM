package com.atm.model.dtos;

import com.atm.model.enums.AccountTypes;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Date;

@Builder @Getter @Setter
@AllArgsConstructor @NoArgsConstructor
// user details will be fetched from auth object. Logged in user
public class AccountDto {
    private String slug;
    private String createdAt;
    private String number;
    private double balance;
    @NonNull @NotEmpty
    private AccountTypes type;
}
