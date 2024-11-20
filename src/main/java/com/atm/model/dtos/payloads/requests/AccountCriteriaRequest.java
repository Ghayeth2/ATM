package com.atm.model.dtos.payloads.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @Builder @AllArgsConstructor
public class AccountCriteriaRequest {
    private String searchQuery;
    private Long userId;
    private String sortBy;
    private String sortOrder;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
