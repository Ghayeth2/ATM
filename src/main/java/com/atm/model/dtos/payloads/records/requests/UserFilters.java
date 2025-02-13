package com.atm.model.dtos.payloads.records.requests;

public record UserFilters(
        String firstName,
        String lastName,
        String from,
        String to
) {
}
