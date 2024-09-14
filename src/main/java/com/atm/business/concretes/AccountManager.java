package com.atm.business.concretes;

import com.atm.business.abstracts.AccountServices;
import com.atm.model.dtos.AccountDto;
import com.atm.model.entities.Account;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountManager implements AccountServices {
    @Override
    public String save(AccountDto dto) {
        return "";
    }

    @Override
    public String update(AccountDto dto, String slug) {
        return "";
    }

    @Override
    public String delete(String slug) {
        return "";
    }

    // in case of other service class requires the model of this class
    @Override
    public Account findBySlug(String slug) {
        return null;
    }

    @Override
    public List<AccountDto> findAll() {
        return List.of();
    }
}
