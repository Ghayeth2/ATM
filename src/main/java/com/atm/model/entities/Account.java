package com.atm.model.entities;

import com.atm.model.enums.AccountTypes;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity @Table(name = "accounts") @Getter
@Builder @AllArgsConstructor
@NoArgsConstructor @Setter
public class Account extends IntermidateBaseEntity{
    @Column(unique = true, nullable = false, length = 20)
    private String number;
    private double balance;
    @Column(length = 30)
    private String currency;
    @Column(nullable = false)
    private String type;
    @ManyToOne
    private User user;
    @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE)
    private List<Transaction> transactions;

    public Account(String s, double v, String s1, String personalAccount, User user) {
    }
}
