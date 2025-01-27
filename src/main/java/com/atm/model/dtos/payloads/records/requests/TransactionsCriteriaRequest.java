package com.atm.model.dtos.payloads.records.requests;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public record TransactionsCriteriaRequest(
        String searchQuery,
        String sortBy,
        String sortOrder,
        LocalDateTime from,
        LocalDateTime to,
        Pageable pageable,
        double startAmount,
        double endAmount
) {
}
