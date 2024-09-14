package com.atm.business.abstracts;

import com.atm.model.dtos.AccountDto;
import com.atm.model.entities.Account;

public interface AccountServices extends CRUDServices<AccountDto, AccountDto, Account> {
}
