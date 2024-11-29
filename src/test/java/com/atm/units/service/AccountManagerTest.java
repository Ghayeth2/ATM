package com.atm.units.service;
import com.atm.business.abstracts.ConfigService;
import com.atm.business.concretes.AccountManager;
import com.atm.business.concretes.ConfigManager;
import com.atm.core.utils.strings_generators.AccountNumberGenerator;
import com.atm.criterias.AccountCriteria;
import com.atm.dao.daos.AccountDao;
import com.atm.model.dtos.payloads.requests.AccountCriteriaRequest;
import com.atm.model.dtos.payloads.responses.AccountDto;
import com.atm.model.dtos.payloads.responses.AccountPageImplRes;
import com.atm.model.entities.Account;
import com.atm.model.entities.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class AccountManagerTest {

    @Mock
    private AccountCriteria accountCriteria;

    @Mock
    private Properties properties;

    @Mock
    private AccountDao accountDao;

    @Mock
    private ConfigService configService;

    @Mock
    private AccountNumberGenerator accountNumberGenerator;

    @InjectMocks
    private AccountManager accountManager;

    @SneakyThrows
    // save an account
    @Test
    void shouldSaveAnAccount_WhenAccountIsProvided() {
        String type = "BUSINESS";
        String currency = "EUR";

        User user = User.builder()
                .email("user@gmail.com").build();
        user.setId(1L);
        when(accountNumberGenerator.accountNumber()).thenReturn(
                "1010-2323-4444-1010"
        );
        accountManager.save(type, currency, user);
        verify(accountDao).save(ArgumentMatchers
                .argThat(ac -> ac.getType().equals(
                        "Business Account")));
    }

    // delete an account
    @Test
    void shouldDeleteAnAccount_WhenSlugIsSent() {
        Account account = Account.builder().build();
        account.setId(1L);
        when(accountDao.findBySlug(anyString()))
                .thenReturn(Optional.of(account));
        doNothing().when(accountDao).deleteById(anyLong());
        String res = accountManager.delete("slug");
        Assertions.assertEquals("Account deleted", res);
    }

    // no such element exception thrower
    @Test
    void shouldThrowNoSuchElementException_WhenAccountDoesNotExist() {
        when(accountDao.findBySlug(anyString())).thenReturn(
                Optional.empty()
        );
        NoSuchElementException ex = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> accountManager.findBySlug("slug")
        );
        Assertions.assertEquals(NoSuchElementException.class, ex.getClass());
    }

    // findBySlug
    @Test
    void shouldFindAnAccount_WhenSlugIsFound() {
        Account account = Account.builder().balance(500).build();
        when(accountDao.findBySlug(anyString()))
                .thenReturn(Optional.of(account));
        Account res = accountManager.findBySlug("slug");
        Assertions.assertEquals(account.getBalance(), res.getBalance());
    }

    // findAll paginated
    @SneakyThrows
    @Test
    void shouldFindAllFilteredPaginatedAndSorted_WhenCertainCriteriaIsProvided() {
        AccountDto account = AccountDto.builder().balance(5000).
                type("Business Account").build();
        List<AccountDto> accounts = List.of(
                account
        );
        Page<AccountDto> page = new PageImpl<>(accounts);
        when(configService.getProperties()).thenReturn(properties);
        when(properties.getProperty(anyString())).thenReturn("5");
        when(accountCriteria.findAllPaginatedAndFiltered(
                any(Pageable.class), any(AccountCriteriaRequest.class)
        )).thenReturn(page);
        Page<AccountDto> res = accountManager.findAll(
                1L, 2, "iii", "number",
                "asc",
                "", ""
        );
        verify(accountCriteria).findAllPaginatedAndFiltered(
                ArgumentMatchers.argThat(
                        pageable -> pageable.getPageNumber() == 1
                ),
                ArgumentMatchers.argThat(
                        req ->
                            req.getStartDate().isBefore(
                                    LocalDateTime.now()
                            )
                )
        );
        verify(accountCriteria).findAllPaginatedAndFiltered(
                ArgumentMatchers.argThat(
                        pageable -> pageable.getPageSize() == 5
                ),
                ArgumentMatchers.argThat(
                        req -> req.getSortBy().equalsIgnoreCase(
                                "number"
                        ) && req.getSortOrder().equalsIgnoreCase(
                                "asc"
                        )
                )
        );
        Assertions.assertEquals(res.getTotalElements(), 1);
    }
}
