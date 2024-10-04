package com.atm.business.concretes;

import com.atm.business.abstracts.AccountServices;
import com.atm.business.abstracts.ConfigService;
import com.atm.core.utils.converter.DateFormat;
import com.atm.core.utils.strings_generators.AccountNumberGenerator;
import com.atm.core.utils.strings_generators.SlugGenerator;
import com.atm.dao.abstracts.AccountDao;
import com.atm.model.dtos.AccountDto;
import com.atm.model.enums.AccountTypes;
import com.atm.model.entities.Account;
import com.atm.model.entities.User;
import com.atm.model.enums.Currencies;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service @AllArgsConstructor @Log4j2
public class AccountManager implements AccountServices {

    private AccountNumberGenerator numberGenerator;
    private DateFormat dateFormat;
    private AccountDao accountDao;
    private SlugGenerator slugGenerator;
    // Getting latest updated value of page size from property file
    // Not using @Value of spring since it only loads data at the time of running the app no more
    private ConfigService configService;

    @Override
    public String save(String  accountType, String currency, User user) throws IOException {
        Currencies cur = Currencies.valueOf(currency);
        String number = numberGenerator.accountNumber();
        log.info("Number of account: " + number+ " length: " + number.length());
        AccountTypes type = AccountTypes.valueOf(accountType);
        Account account = Account.builder()
                .type(type.getContent())
                .currency(cur.getSymbol())
                .balance(0.0)
                .number(number)
                .user(user)
        .build();
        account.setSlug(slugGenerator.slug(account.getNumber()));
        accountDao.save(account);
        return type.getContent();
    }


    @Transactional
    @Override
    public String delete(String slug) {
        Optional<Account> account = accountDao.findBySlug(slug);
//        account.setId(1L);

        log.info("account: " + account.get().getId());
        accountDao.deleteById(account.get().getId());
        return "Account deleted";
    }

    // in case of other service class requires the model of this class
    @Override
    public Account findBySlug(String slug) {
        return accountDao.findBySlug(slug).orElseThrow();
    }

    @Override
    public Page<AccountDto> findAll(Long user, int page, String searchQuery,
                                    String sortBy, String order, String  from, String  to) throws IOException, ParseException {

        // Fetching page size from dynamic-configs.properties file
        int pageSize = Integer.parseInt(configService.getProperties().getProperty("page.size"));
        // If Dates are not sat by user, use default dates (within a month ago)
        Date startDate;
        Date endDate;
        if (from.isEmpty() && to.isEmpty()) {
            Calendar calendar = Calendar.getInstance();

            // Set endDate to today's date and truncate to yyyy-MM-dd (midnight)
            endDate = truncateToDate(calendar.getTime());

            // Subtract one month for the startDate and truncate to yyyy-MM-dd (midnight)
            calendar.add(Calendar.MONTH, -1);
            startDate = truncateToDate(calendar.getTime());
            log.info("Start date: " + startDate);
            log.info("End date: " + endDate);
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            startDate = formatter.parse(from);
            endDate = formatter.parse(to);
        }
        // Setting the sort order and by parameters (if no sort is chosen by user) default sort is By create date in descending order
        Sort sort;
        if (!order.isEmpty())
            sort = order.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        else
            sort = Sort.by("createdDate").descending();
        // Setting the page requirements (offset & size) and setting the sort value.
        log.info("page size : " + pageSize);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        // Calling custom dao method to retrieve accounts
        return accountDao.findAllPaginatedAndFiltered(pageable, user, searchQuery, startDate, endDate );
    }

    private static Date truncateToDate(Date date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = formatter.format(date); // Convert Date to yyyy-MM-dd format string
        return formatter.parse(formattedDate); // Convert the string back to a Date (with time 00:00:00)
    }
}
