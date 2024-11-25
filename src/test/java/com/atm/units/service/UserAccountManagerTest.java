package com.atm.units.service;

import com.atm.business.concretes.UserAccountServicesManager;
import com.atm.dao.daos.UserDao;
import com.atm.model.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserAccountManagerTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserAccountServicesManager userAccountServicesManager;

    // increasing failed attempts counter
    @Test
    void shouldIncreaseFailedAttempts_WhenLoginFailureOccurs() {
        // the code is two lines, actual service doesn't return
        User user = User.builder().firstName("first")
                .lastName("last").email("email@gmail.com")
                .password("pass").build();
        userAccountServicesManager.increaseFailedAttempts(
                user
        );
        Mockito.verify(userDao).updateFailedAttempts(
                         ArgumentMatchers.eq(1),
                         ArgumentMatchers.contains(user.getEmail())
        );
    }

    // reset failed attempts counter
    @Test
    void shouldResetFailedAttempts_WhenLoginSuccessful(){
        // Need to verify the method is being called and executed
        // Arrange & Execute
        userAccountServicesManager.resetFailedAttempts(
                "test@gmail.com"
        );
        // Assert execution
        Mockito.verify(userDao).updateFailedAttempts(
                ArgumentMatchers.eq(0),
                ArgumentMatchers.contains("test@gmail.com")
        );
    }

    // lock
    @Test
    void shouldLockUserAccount_WhenLoginAttemptsAreOut() {
        // locking user via save() method
        // no other services called
        // save() does not return any thing
        // the service method returns nothing
        // TODO: Mockito.verify()
        User user = User.builder().email("test@gmail.com")
                .password("pass").build();
        userAccountServicesManager.lock(user);
        Mockito.verify(userDao).save(ArgumentMatchers.eq(user));
    }

    // unlockWhenTimeExpired
    @Test
    void shouldUnlockUserAccount_WhenTimeIsExpired() {
        // Arrange
        Calendar calendar = Calendar.getInstance();
        calendar.set(2022, Calendar.JANUARY, 1);
//        log.info("date: {}", date);
        User user = User.builder().email("test@gmail.com")
                .lockTime(calendar.getTime()).build();
        log.info("date: {}", user.getLockTime());
        boolean res = userAccountServicesManager.unlockWhenTimeExpired(user);
        Assertions.assertThat(res).isTrue();
    }

    // find by email
    @Test
    void shouldReturnUser_WhenEmailIsSent() {
        User user = User.builder().email("test@gmail.com")
                .password("pass").build();
        when(userDao.findByEmail(anyString())).thenReturn(user);
        User res = userAccountServicesManager.findByEmail(anyString());
        Assertions.assertThat(res).isNotNull();
    }

    // activating the account
    @Test
    void shouldActivateUserAccount_WhenEmailIsVerified() {
        // Arrange
        User user = User.builder().firstName("first").lastName("last")
                .email("email@gmail.com").password("pass").build();
        user.setId(1L);
        // when Triggered doSomething / doNothing
        when(userDao.findById(anyLong())).thenReturn(Optional.of(user));
        // Calling the service
        userAccountServicesManager.activateAccount(user);
        // Assert it has reached userDao.save()
        Mockito.verify(userDao).save(ArgumentMatchers.eq(user));
    }
}
