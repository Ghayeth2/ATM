package com.atm.units.service;
import com.atm.business.abstracts.UserAccountServices;
import com.atm.business.concretes.ConfirmationTokenManager;
import com.atm.core.exceptions.AccountInactiveException;
import com.atm.dao.daos.ConfirmationTokenDao;
import com.atm.model.entities.ConfirmationToken;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Calendar;

import static org.mockito.ArgumentMatchers.anyString;
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

    private ConfirmationToken token;

    @BeforeEach
    void setUp() {
        token = ConfirmationToken.builder().token("token")
                .email("test@gmail.com").build();
    }

    // save confirmation Token
    @Test
    void shouldSaveConfirmationToken_WhenConfirmationTokenIsGiven() {
        confirmationTokenManager.saveConfirmationToken(token);
//        doNothing().when(confirmationTokenDao).save(any(ConfirmationToken.class));
        verify(confirmationTokenDao).save(ArgumentMatchers
                .eq(token));
    }

    // find by token
    @Test
    void shouldReturnToken_WhenTokenIsGiven() {
        when(confirmationTokenDao.findByToken(anyString()))
                .thenReturn(token);
        ConfirmationToken res = confirmationTokenManager.findByToken("token");
        Assertions.assertEquals(token.getToken(),
                res.getToken());
    }

    // is token valid
    @Test
    void shouldReturnTokenIsValid_WhenTokenIsNotExpired() {
        LocalDateTime expiresAt =
                LocalDateTime.now().plusMinutes(30);
        token.setExpiresAt(expiresAt);
        when(confirmationTokenDao.findByToken(anyString()))
        .thenReturn(token);
        boolean res = confirmationTokenManager.isTokenValid("token");
        Assertions.assertTrue(res);
    }

    @Test
    void shouldReturnTokenIsInvalid_WhenTokenIsExpired() {
        token.setExpiresAt(
                LocalDateTime.now().minusMinutes(1)
        );
        when(confirmationTokenDao.findByToken(anyString()))
        .thenReturn(token);
        boolean res = confirmationTokenManager.isTokenValid("token");
        Assertions.assertFalse(res);
    }

    // confirm token
    @SneakyThrows
    @Test
    void shouldConfirmToken_WhenTokenIsValid() {
        token.setExpiresAt(
                LocalDateTime.now().plusMinutes(1)
        );
        when(confirmationTokenDao.findByToken(anyString()))
                .thenReturn(token);
        when(confirmationTokenDao.save(
                any()
        )).thenReturn(token);
        String res = confirmationTokenManager.confirmToken(token.getToken());
        Assertions.assertEquals(token.getToken(), res);
    }

    // Testing when the email is already confirmed!
    @Test
    void shouldThrowException_WhenEmailIsAlreadyConfirmed() {
        token.setExpiresAt(
                LocalDateTime.now().plusMinutes(1)
        );
        token.setConfirmedAt(
                LocalDateTime.now()
        );
        when(confirmationTokenDao.findByToken(anyString()))
                .thenReturn(token);
        AccountInactiveException ex =
                Assertions.assertThrows(
                        AccountInactiveException.class,
                        () ->confirmationTokenManager
                                .confirmToken(token.getToken())
                );
        Assertions.assertEquals("Email is already confirmed."
        , ex.getMessage());
    }
}
