package com.atm.business.abstracts;

import com.atm.model.dtos.payloads.responses.AccountDto;
import com.atm.model.entities.Account;
import com.atm.model.entities.User;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

// For Admin panel, returning all data do it from DAO of user
public interface AccountServices  {
    String delete(String slug);
    Account findByNumber(String number);
    Page<AccountDto> findAll(Long user, int page, String searchQuery,
                             String sortBy, String order, String  from,
                             String  to) throws IOException, ParseException;
    Account findBySlug(String slug);

    /**
     * Handles actual interaction with account's balance, and returns
     * the balance after the interaction is made.
     * @param number
     * @param amount
     * @return
     */
    double deposit(String number, Double amount);

    /**
     * Handles actual interaction with account's balance, and returns
     * the balance after the interaction is completed.
     * @param number
     * @param amount
     * @return
     */
    double withdraw(String number, Double amount);
    void save(String accountType, String currency, User user) throws IOException;
}
