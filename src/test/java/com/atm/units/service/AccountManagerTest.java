package com.atm.units.service;
import com.atm.business.abstracts.ConfigService;
import com.atm.business.concretes.AccountManager;
import com.atm.criterias.AccountCriteria;
import com.atm.dao.daos.AccountDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class AccountManagerTest {

    @Mock
    private AccountCriteria accountCriteria;

    @Mock
    private AccountDao accountDao;

    @Mock
    private ConfigService configService;

    @InjectMocks
    private AccountManager accountManager;

    // save an account
    @Test
    void shouldSaveAnAccount_WhenAccountIsProvided() {

    }

    // delete an account
    @Test
    void shouldDeleteAnAccount_WhenSlugIsSent() {

    }

    // findBySlug
    @Test
    void shouldFindAnAccount_WhenSlugIsFound() {

    }

    // findAll paginated
    @Test
    void shouldFindAllFilteredPaginatedAndSorted_WhenCertainCriteriaIsProvided() {

    }
}
