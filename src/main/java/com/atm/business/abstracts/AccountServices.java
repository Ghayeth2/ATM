package com.atm.business.abstracts;

import com.atm.model.dtos.AccountDto;
import com.atm.model.dtos.AccountTypes;
import com.atm.model.entities.Account;
import com.atm.model.entities.User;

import java.io.IOException;
import java.util.List;

public interface AccountServices  {
    String delete(String slug);
    List<AccountDto> findAll(User user);
    String update(AccountTypes type, String slug);
    Account findBySlug(String slug);
    String save(AccountTypes accountType, User user) throws IOException;
}
