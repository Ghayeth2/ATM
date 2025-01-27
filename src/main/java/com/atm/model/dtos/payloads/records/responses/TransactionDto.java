package com.atm.model.dtos.payloads.records.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

// Search only will have type param, and sort by Date, for the user
// JPQL will do the job.
@Getter @Setter @Builder
@AllArgsConstructor
public class TransactionDto {
    private String slug;
    private String type;
    private LocalDateTime createdDate;
    private String receiptUrl;
    private double amount;
    private double balanceAfter;
    private String formattedDate;

    public TransactionDto(String slug,
                          String type,
                          LocalDateTime createdDate,
                          String receiptUrl,
                          double amount,
                          double balanceAfter) {
        this.slug = slug;
        this.type = type;
        this.createdDate = createdDate;
        this.receiptUrl = receiptUrl;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }
}
