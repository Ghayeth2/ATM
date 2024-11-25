package com.atm.units.repository;

import com.atm.core.config.TestAuditingConfig;
import com.atm.core.utils.converter.DateFormatConverter;
import com.atm.core.utils.strings_generators.SlugGenerator;
import com.atm.criterias.AccountCriteria;
import com.atm.dao.daos.AccountDao;
import com.atm.dao.daos.UserDao;
import com.atm.model.dtos.payloads.requests.AccountCriteriaRequest;
import com.atm.model.dtos.payloads.responses.AccountDto;
import com.atm.model.entities.Account;
import com.atm.model.entities.User;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
@DataJpaTest by default loads only Jpa related
configurations and classes, not all the application
context, so if i am using in my test class other dependencies
I need to import them using @Import

Down in my test class I included AccountCriteria which is custom
Repo, so it is not extending a JPA interface. Therefore, spring
won't load it by default.
Same for DateFormatConverter, it is a util class not a JPA repo.
 */
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
@Import({TestAuditingConfig.class,
        AccountCriteria.class,
        DateFormatConverter.class,
        SlugGenerator.class})
public class AccountCriteriaTest {
    @Autowired
    private SlugGenerator slug;
    @Autowired
    private AccountCriteria accountCriteria;
    @Autowired
    private UserDao userDao;
    @Autowired
    private DateFormatConverter formatter;
    @Autowired
    private AccountDao accountDao;
    private List<Account> accounts = new ArrayList<>();
    private User user;
    private User usr;
    @BeforeEach
    void setUp() {
        // setting up two users for accounts lists
        user = User.builder()
                .email("ghayeth@gmail.com").firstName("Ghayeth")
                .lastName("almasri").password("pass")
                .accountNonLocked(1).failedAttempts(0).enabled(false)
                .build();
        user.setSlug("user-slug");
        // 2nd user
        usr = User.builder()
                .email("muhammad@gmail.com").firstName("muhammad")
                .lastName("almasri").password("pass")
                .accountNonLocked(1).failedAttempts(0).enabled(false)
                .build();
        usr.setSlug("usr-slug");
        // Creating two user accounts
        userDao.saveAll(Arrays.asList(user, usr));
        // Setting up two account lists
        accounts.addAll(Stream.of(
                new Account("1000-2020-2323-1001",
                        0.0, "!", "Personal Account", user),
                new Account("1000-3242-2034-1002",
                        0.0, "#", "Business Account", user),
                new Account("1000-4788-9099-1003",
                        0.0, "&", "Savings Account", user),
                new Account("1000-5033-9099-1004",
                        0.0, "#", "Personal Account", user),
                new Account("1000-8022-1331-1005",
                        0.0, "&", "Business Account", user),
                new Account("1000-7221-7044-1006",
                        0.0, "&", "Personal Account", user),
                new Account("1000-4114-6226-1007",
                        0.0, "&", "Business Account", user)
        ).toList());
        // Setting the slug (identifier) of each account
        accounts.forEach(account -> {account.setSlug(account.getNumber());});
        accountDao.saveAll(accounts);
        // Clearing accounts to add new list
        accounts.clear();
        // New list
        accounts.addAll(Stream.of(
                new Account("1000-2121-3131-1008",
                        0.0, "!", "Personal Account", usr),
                new Account("1000-8435-2023-1009",
                        0.0, "#", "Business Account", usr),
                new Account("1000-8351-9909-1010",
                        0.0, "&", "Savings Account", usr)
        ).toList());
        // Setting the slug value for each account
        accounts.forEach(account -> {
            account.setSlug(account.getNumber());
        });
        // Saving the 2nd list
        accountDao.saveAll(accounts);
        // Clearing the list
        accounts.clear();
    }

    // When testing the method of dynamic queries,
    // provide for each condition u expect a separated test
    @Test
    void shouldReturnPageOfUserAccountsWithinOneMonth_WhenNoParametersAreProvided() {
        LocalDateTime startDate = formatter.formatRequestDate(LocalDateTime.now()
                .minusMonths(1));
        LocalDateTime endDate = formatter.formatRequestDate(LocalDateTime.now());
        Pageable pageable = PageRequest.of(0, 5);
        Page<AccountDto> returnedAccounts = accountCriteria.findAllPaginatedAndFiltered(
                pageable, new AccountCriteriaRequest("", 1L,
                        "createdDate", "desc",
                        startDate,
                        endDate)
        );
        Assertions.assertThat(returnedAccounts.getTotalElements())
                .isEqualTo(7);
    }

    // TODO: search by number & sort by number
    @Test
    void shouldReturnThreeSortedRecords_WhenSearchAndSortByNumber() {
        // Returning personal accounts of first user
        LocalDateTime startDate = formatter.formatRequestDate(LocalDateTime.now()
                .minusMonths(1));
        LocalDateTime endDate = formatter.formatRequestDate(LocalDateTime.now());
        Pageable pageable = PageRequest.of(0, 5);
        List<AccountDto> res = accountCriteria.findAllPaginatedAndFiltered(
                pageable, new AccountCriteriaRequest("22", 1L,
                        "type", "desc",
                        startDate,
                        endDate)
        ).stream().toList();
        res.forEach(ac -> System.out.println(ac.getNumber()+" "+ac.getType()));
        Assertions.assertThat(res.get(0).getType()).isEqualTo("Personal Account");
    }

    // How many pages are returned without any parameters
    // The date config and page size (are dynamically set by service layer)
    @Test
    void shouldReturnTwoPages_WhenSizeIsFive(){
        LocalDateTime startDate = formatter.formatRequestDate(LocalDateTime.now()
                .minusMonths(1));
        LocalDateTime endDate = formatter.formatRequestDate(LocalDateTime.now());
        Pageable pageable = PageRequest.of(0, 5);
        Page<AccountDto> returnedAccounts = accountCriteria.findAllPaginatedAndFiltered(
                pageable, new AccountCriteriaRequest("", 1L,
                        "createdDate", "desc",
                        startDate,
                        endDate)
        );
        Assertions.assertThat(returnedAccounts.getTotalPages())
                .isEqualTo(2);
    }

    // Offset testing

    // Sorted ASC by account type
    @Test
    void shouldReturnSortedByType_WhenSortFieldIsType() {
        // Dynamic data simulation
        LocalDateTime startDate = formatter.formatRequestDate(LocalDateTime.now())
                .minusMonths(1);
        LocalDateTime endDate = formatter.formatRequestDate(LocalDateTime.now());
        // Dynamic page size & number
        Pageable pageable = PageRequest.of(0, 5);
        List<AccountDto> res = accountCriteria.findAllPaginatedAndFiltered(pageable,
                new AccountCriteriaRequest(
                        "", 2L, "type", "asc"
                        , startDate, endDate
                )).stream().toList();
        Assertions.assertThat(res.get(0).getType()).isEqualTo("Business Account");
    }

    // Sorted by date
    @Test
    void shouldReturnSortedByDate_WhenNoSortParameterIsProvided() {
        LocalDateTime startDate = formatter.formatRequestDate(LocalDateTime.now()
                .minusMonths(1));
        LocalDateTime endDate = formatter.formatRequestDate(LocalDateTime.now());
        Pageable pageable = PageRequest.of(0, 5);
        List<AccountDto> res = accountCriteria.findAllPaginatedAndFiltered(
                pageable, new AccountCriteriaRequest("", 1L,
                        "createdDate", "desc",
                        startDate,
                        endDate)
        ).stream().toList();
        Assertions.assertThat(res.get(0).getCreatedDate()).isAfter(
                res.get(1).getCreatedDate()
        );
    }

    // Sorted by currency / since currencies might have equal size
    // only one is different than the others (!)
    @Test
    void shouldReturnSortedByCurrency_WhenSortFieldIsCurrency() {
        // Dynamic start & end dates are usually configured by service class
        LocalDateTime startDate = formatter.formatRequestDate(LocalDateTime.now())
                .minusMonths(1);
        LocalDateTime endDate = formatter.formatRequestDate(LocalDateTime.now());
        // Dynamic page size is extracted from properties file.
        Pageable pageable = PageRequest.of(0, 5);
        // Converting response page into List
        List<AccountDto> res = accountCriteria.findAllPaginatedAndFiltered(pageable,
                new AccountCriteriaRequest("", 1L, "currency",
                        "asc", startDate, endDate)).stream().toList();
        // Asserting the result is correct
        System.out.println("The size of the list: "+res.size());
        Assertions.assertThat(res.get(0).getNumber()).isEqualTo(
                "1000-2020-2323-1001"
        );

    }

    // Sorted by number DESC / ASC
    @Test
    void shouldReturnSortedByNumber_WhenSortFieldIsNumber() {
        LocalDateTime startDate = formatter.formatRequestDate(LocalDateTime.now()
                .minusMonths(1));
        LocalDateTime endDate = formatter.formatRequestDate(LocalDateTime.now());
        Pageable pageable = PageRequest.of(0, 5);
        List<AccountDto> res = accountCriteria.findAllPaginatedAndFiltered(
                pageable, new AccountCriteriaRequest("", 1L,
                        "number", "desc",
                        startDate,
                        endDate)
        ).stream().toList();
//        System.out.println("number "+res.get(0).getNumber());
        Assertions.assertThat(res.get(0).getNumber()).isEqualTo("1000-8022-1331-1005");
    }

    @Test
    void shouldReturnCertainAccounts_WhenAccountTypeIsProvided() {
        // Returning personal accounts of first user
        LocalDateTime startDate = formatter.formatRequestDate(LocalDateTime.now()
                .minusMonths(1));
        LocalDateTime endDate = formatter.formatRequestDate(LocalDateTime.now());
        // U have limited the size of 5 members for each page
        Pageable pageable = PageRequest.of(0, 5);
        // TODO: when searching by type, u should match cases 4 both (table & search query)
        // It is working, but now we r dealing directly with tables. So no ignoreCase() method.
        List<AccountDto> res = accountCriteria.findAllPaginatedAndFiltered(
                pageable, new AccountCriteriaRequest("Sa", 1L,
                        "createdDate", "desc",
                        startDate,
                        endDate)
        ).stream().toList();
        System.out.println("The size of the list: "+res.size());
        Assertions.assertThat(res.size()).isEqualTo(1);
    }

}
