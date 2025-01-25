package com.atm.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.websocket.Encoder;
import lombok.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@Entity @Builder @Getter
@Setter @AllArgsConstructor
@NoArgsConstructor
public class Transaction extends IntermidateBaseEntity{
    @NotNull
    @Column(columnDefinition = "TEXT")
    private String receiptUrl;
    private String type;
    private double amount;
    private double balanceAfter;
    @ManyToOne
    private Account account;
}
