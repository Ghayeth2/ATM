package com.atm.business.abstracts;

import com.atm.model.dtos.AccountDto;
import com.atm.model.enums.AccountTypes;
import com.atm.model.entities.Account;
import com.atm.model.entities.User;

import java.io.IOException;
import java.util.List;

// For Admin panel, returning all data do it from DAO of user
public interface AccountServices  {
    String delete(String slug);
    List<AccountDto> findAll(User user);
    Account findBySlug(String slug);
    String save(String accountType, String currency, User user) throws IOException;
}
