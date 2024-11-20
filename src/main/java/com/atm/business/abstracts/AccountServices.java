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
    Page<AccountDto> findAll(Long user, int page, String searchQuery,
                             String sortBy, String order, String  from,
                             String  to) throws IOException, ParseException;
    Account findBySlug(String slug);
    String save(String accountType, String currency, User user) throws IOException;
}
