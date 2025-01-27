package com.atm.model.dtos.payloads.responses;

import lombok.Getter;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
public class TransactionsPageImpRes<T> extends PageImpl<T> {
    private int totalPages;

    public TransactionsPageImpRes(int totalPages, long totalElements, List<T> content, Pageable pageable) {
        super(content, pageable, totalElements);
        this.totalPages = totalPages;
    }
}
