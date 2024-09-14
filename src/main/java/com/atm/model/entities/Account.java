package com.atm.model.entities;

import com.atm.model.dtos.AccountTypes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity @Table(name = "accounts") @Getter
@Builder @AllArgsConstructor
@NoArgsConstructor @Setter
public class Account extends IntermidateBaseEntity{
    @Column(unique = true, nullable = false, length = 16)
    private String number;
    private double balance;
    @Column(nullable = false)
    private AccountTypes type;
    @ManyToOne
    private User user;
}
