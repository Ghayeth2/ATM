package com.atm.model.dtos.payloads.records.requests;

public record TransactionsFiltersRequest(
        String searchQuery,
        String sortBy,
        String sortOrder,
        String fromDate,
        String toDate,
        double fromAmount,
        double toAmount,
        int page
) {
}
