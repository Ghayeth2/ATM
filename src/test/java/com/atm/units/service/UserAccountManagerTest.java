package com.atm.units.service;

import com.atm.business.concretes.UserAccountServicesManager;
import com.atm.dao.daos.UserDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class UserAccountManagerTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserAccountServicesManager userAccountServicesManager;

    // increasing failed attempts counter
    @Test
    void shouldIncreaseFailedAttempts_WhenLoginFailureOccurs() {

    }

    // reset failed attempts counter
    @Test
    void shouldResetFailedAttempts_WhenLoginSuccessful(){

    }

    // lock
    @Test
    void shouldLockUserAccount_WhenLoginAttemptsAreOut() {

    }

    // unlockWhenTimeExpired
    @Test
    void shouldUnlockUserAccount_WhenTimeIsExpired() {

    }

    // find by email
    @Test
    void shouldReturnUser_WhenEmailIsSent() {

    }

    // activating the account
    @Test
    void shouldActivateUserAccount_WhenEmailIsVerified() {

    }
}
