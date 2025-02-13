package com.atm.model.dtos.payloads.records.requests;

public record AccountFilters(
        String email,
        String type,
        String from,
        String to
) {
}
