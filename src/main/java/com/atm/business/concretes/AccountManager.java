package com.atm.business.concretes;

import com.atm.business.abstracts.AccountServices;
import com.atm.core.utils.strings_generators.AccountNumberGenerator;
import com.atm.core.utils.strings_generators.SlugGenerator;
import com.atm.dao.abstracts.AccountDao;
import com.atm.model.dtos.AccountDto;
import com.atm.model.enums.AccountTypes;
import com.atm.model.entities.Account;
import com.atm.model.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Service @AllArgsConstructor
public class AccountManager implements AccountServices {

    private AccountNumberGenerator numberGenerator;
    private AccountDao accountDao;
    private SlugGenerator slugGenerator;

    @Override
    public String save(String  accountType, User user) throws IOException {
        AccountTypes type = AccountTypes.valueOf(accountType);
        Account account = Account.builder()
                .type(type.getContent())
                .balance(0.0)
                .number(numberGenerator.accountNumber())
                .user(user)
        .build();
        account.setSlug(slugGenerator.slug(account.getNumber()));
        accountDao.save(account);
        return type.getContent();
    }


    @Override
    public String delete(String slug) {
        Account account = accountDao.findBySlug(slug).orElseThrow();
        accountDao.delete(account);
        return "Account deleted";
    }

    // in case of other service class requires the model of this class
    @Override
    public Account findBySlug(String slug) {
        return accountDao.findBySlug(slug).orElseThrow();
    }

    @Override
    public List<AccountDto> findAll(User user) {
        // Use Pageable for breaking data
        Account accProbe = Account.builder().user(user).build();
        // Filtering only this user's accounts
        Example<Account> example = Example.of(accProbe);
        // Initializing DateFormat class
        DateFormat formater = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        return accountDao.findAll(example).stream().
                map(account -> AccountDto.builder()
//                       .type(account.getType())
                        .createdAt(formater.format(account.getCreatedDate()))
                       .slug(account.getSlug())
                       .number(account.getNumber())
                       .balance(account.getBalance())
                .build()).toList();
    }
}
