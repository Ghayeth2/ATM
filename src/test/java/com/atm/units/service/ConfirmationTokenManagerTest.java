package com.atm.units.service;
import com.atm.business.abstracts.UserAccountServices;
import com.atm.business.concretes.ConfirmationTokenManager;
import com.atm.dao.daos.ConfirmationTokenDao;
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
public class ConfirmationTokenManagerTest {

    @Mock
    private ConfirmationTokenDao confirmationTokenDao;

    @Mock
    private UserAccountServices userAccountServices;

    @InjectMocks
    private ConfirmationTokenManager confirmationTokenManager;

    // save confirmation Token
    @Test
    void shouldSaveConfirmationToken_WhenConfirmationTokenIsGiven() {

    }

    // create confirmation token
    @Test
    void shouldCreateConfirmationToken_WhenMethodIsCalled() {

    }

    // find by token
    @Test
    void shouldReturnToken_WhenTokenIsGiven() {

    }

    // is token valid
    @Test
    void shouldValidateToken_WhenMethodIsCalled() {

    }

    // confirm token
    @Test
    void shouldConfirmToken_WhenTokenIsValid() {

    }
}
