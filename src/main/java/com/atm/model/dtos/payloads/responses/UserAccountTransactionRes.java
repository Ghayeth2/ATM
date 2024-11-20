package com.atm.model.dtos.payloads.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// In transactions add view details
// in details show >> from , to , amount, date. account numbers.
// this class will be used in API since i will add the filtering feature.
// User it to return using JPQL projection into DTO
// and make sure it is working with API & AJAX
// When coming to that bridge try with below names if projection will work or not
@Getter @Setter @AllArgsConstructor
public class UserAccountTransactionRes {
    private String fullName;
    private String email;
    private String accountType;
    private String transactionType;
    private double amount;
}
