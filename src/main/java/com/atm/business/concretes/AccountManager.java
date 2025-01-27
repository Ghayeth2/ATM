package com.atm.business.concretes;

import com.atm.business.abstracts.AccountServices;
import com.atm.business.abstracts.ConfigService;
import com.atm.core.utils.converter.DateFormatConverter;
import com.atm.core.utils.strings_generators.AccountNumberGenerator;
import com.atm.core.utils.strings_generators.StringGenerator;
import com.atm.dao.criterias.AccountCriteria;
import com.atm.dao.daos.AccountDao;
import com.atm.model.dtos.payloads.requests.AccountCriteriaRequest;
import com.atm.model.dtos.payloads.responses.AccountDto;
import com.atm.model.enums.AccountTypes;
import com.atm.model.entities.Account;
import com.atm.model.entities.User;
import com.atm.model.enums.Currencies;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service @RequiredArgsConstructor
@Log4j2
public class AccountManager implements AccountServices {

    private final AccountCriteria accountCriteria;
    private final AccountDao accountDao;
    // Getting latest updated value of page size from property file
    // Not using @Value of spring since it only loads data at the time of running the app no more
    private final ConfigService configService;
    private final AccountNumberGenerator accountNumberGenerator;
    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public void save(String  accountType, String currency, User user) throws IOException {
        Currencies cur = Currencies.valueOf(currency);
        String number = accountNumberGenerator.accountNumber();
        log.info("Number of account: " + number+ " length: " + number.length());
        AccountTypes type = AccountTypes.valueOf(accountType);
        Account account = Account.builder()
                .type(type.getContent())
                .currency(cur.getName())
                .balance(0.0)
                .number(number)
                .user(user)
        .build();
        account.setSlug(new StringGenerator().slug(account.getNumber()));
        accountDao.save(account);
    }


    @Transactional
    @Override
    public String delete(String slug) {
        Optional<Account> account = accountDao.findBySlug(slug);

        log.info("account id to be deleted: " + account.get().getId());
        Account accountToDelete = entityManager.merge(account.get());
        accountDao.delete(accountToDelete);
        return "Account deleted";
    }

    @Override
    public Account findByNumber(String number) {
        return accountDao.findByNumber(number).orElse(null);
    }


    // in case of other service class requires the model of this class
    @Override
    public Account findBySlug(String slug) {
        return accountDao.findBySlug(slug).orElseThrow();
    }

    /**
     * Actual interaction with account record, will add
     * funds to target account.
     * @param number
     * @param amount
     * @return
     */
    @Override
    public double deposit(String number, Double amount) {
        // Retrieving account
        Account account = findByNumber(number);
        // Adding funds
        double currentBalance = account.getBalance()
                + amount;
        account.setBalance(currentBalance);
        // Saving new account's data after changes
        accountDao.save(account);
        return currentBalance;
    }

    /**
     * Actual interaction with account record, to withdraw
     * funds.
     * @param number
     * @param amount
     */
    @Override
    public double withdraw(String number, Double amount) {
        // Retrieving account
        Account account = findByNumber(number);
        // Withdrawing funds
        double currentBalance = account.getBalance()
                - amount;
        account.setBalance(currentBalance);
        // Saving changes
        accountDao.save(account);
        return currentBalance;
    }

    @Override
    public Page<AccountDto> findAll(Long user,
                                    int page,
                                    String searchQuery,
                                    String sortBy,
                                    String order,
                                    String  from,
                                    String  to) throws IOException, ParseException {

        // Date formatter
        DateFormatConverter formatter = new DateFormatConverter();
        // Fetching page size from dynamic-configs.properties file

        int pageSize = Integer.parseInt(configService.
                getProperties().getProperty("accounts.page.size"));

        // If Dates are not sat by user, use default dates (within a month ago)
        LocalDateTime startDate;
        LocalDateTime endDate;
        if (from.isEmpty() && to.isEmpty()) {
            LocalDateTime monthAgo = LocalDateTime.now();
            monthAgo = monthAgo.minusMonths(1);
            startDate = formatter.formatRequestDate(monthAgo);
//            log.info("startDate: " + startDate);
            endDate = formatter.formatRequestDate(LocalDateTime.now());
        } else {
            startDate = formatter.formatRequestDate(from);
            endDate = formatter.formatRequestDate(to);
        }
        // Setting the sort order and by parameters (if no sort is chosen by user) default sort is By create date in descending order
        // sort by: default(createdDate), type, balance, number
        if (sortBy.isEmpty() && order.isEmpty()) {
            sortBy = "createdDate";
            order = "desc";
        }
//        log.info("sortBy: " + sortBy + " order: " + order);
        // Setting the page requirements (offset & size) and setting the sort value.

        AccountCriteriaRequest request = AccountCriteriaRequest.builder()
                .startDate(startDate)
                .endDate(endDate)
                .userId(user)
                .searchQuery(searchQuery)
                .sortBy(sortBy)
                .sortOrder(order)
                .build();
        log.info("sortBy: "+ request.getSortBy());
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        return accountCriteria.findAllPaginatedAndFiltered(pageable, request);
//        return accountDao.findAll(pageable).map(account ->
//                AccountDto.builder()
//                        .createdDate(formatter.formatDate(account.getCreatedDate()))
//                        .slug(account.getSlug())
//                        .type(account.getType())
//                        .number(account.getNumber())
//                        .currency(account.getCurrency())
//                        .build());

    }

}
