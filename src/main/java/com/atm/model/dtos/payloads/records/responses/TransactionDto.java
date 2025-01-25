package com.atm.model.dtos.payloads.records.responses;

import java.time.LocalDateTime;

// Search only will have type param, and sort by Date, for the user
// JPQL will do the job.
public record TransactionDto(
        String slug,
        String type,
        LocalDateTime createdDate,
        String receiptUrl,
        double amount,
        double balanceAfter
) {
}
