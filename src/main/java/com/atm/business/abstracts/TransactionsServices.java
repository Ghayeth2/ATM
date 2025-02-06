package com.atm.business.abstracts;


import com.atm.model.dtos.payloads.records.requests.TransactionsFiltersRequest;
import com.atm.model.dtos.payloads.responses.TransactionDto;
import com.atm.model.dtos.payloads.responses.UserAccountTransaction;
import org.springframework.data.domain.Page;

public interface TransactionsServices {
    // Will use join, since User & Transaction not directly connect

    /**
     * The service bellow is used only by Admins,
     * to access user's daily transactions.
     * @param filters
     * @return Page<UserAccountTransaction> record
     */
    Page<UserAccountTransaction> findAllFiltered(TransactionsFiltersRequest
                                               filters);

    /**
     * It will return each account's transactions.
     * @param accountSlug
     * @return Page<Transaction> record
     */
    Page<TransactionDto> findAllByAccount(String accountSlug,
                                          String startDate,
                                          String endDate,
                                          int page,
                                          String sortOrder,
                                          String sortBy);

    /**
     * Will create new transaction using passed
     * parameters from controller, and create its
     * receipt file.
     * @param type
     * @param amount
     * @return statusMessage
     */
    String newTransaction(String type, String amount, String... numbers);

}
