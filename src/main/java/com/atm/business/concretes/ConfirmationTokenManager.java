package com.atm.business.concretes;

import com.atm.business.abstracts.ConfirmationTokenServices;
import com.atm.business.abstracts.UserAccountServices;
import com.atm.core.exceptions.AccountInactiveException;
import com.atm.dao.daos.ConfirmationTokenDao;
import com.atm.model.dtos.TempUser;
import com.atm.model.entities.ConfirmationToken;
import com.atm.model.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ConfirmationTokenManager implements ConfirmationTokenServices {

    private static final Logger log = LoggerFactory.getLogger(ConfirmationTokenManager.class);
    private final ConfirmationTokenDao confirmationTokenDao;
    @Value("${verification.code.expiration}")
    int verificationCodeExpiration;

    public ConfirmationTokenManager(ConfirmationTokenDao confirmationTokenDao) {
        this.confirmationTokenDao = confirmationTokenDao;
    }

    @Override
    public void saveConfirmationToken(ConfirmationToken confirmationToken) {
        System.out.println(confirmationToken.getToken()+" " +
                "is saved to Redis Server for "+ confirmationToken.getEmail());
        confirmationTokenDao.save(confirmationToken);
    }

    @Override
    public ConfirmationToken newConfirmationToken(String email) {
        System.out.println("Token is created for "+email);
        String token = UUID.randomUUID().toString();
        return ConfirmationToken.builder()
                .token(token)
                .expiresAt(LocalDateTime.now().plusMinutes(verificationCodeExpiration))
                .email(email)
                .build();
    }

    @Override
    public ConfirmationToken findByToken(String token) {
        return confirmationTokenDao.findByToken(token);
    }

    @Override
    public boolean isTokenValid(String token) {
        ConfirmationToken cToken = confirmationTokenDao.findByToken(token);
        return !cToken.getExpiresAt().isBefore(
                LocalDateTime.now()
        );
    }


    @Override
    public String  confirmToken(String token) throws AccountInactiveException {
        // TODO: Confirm token
        ConfirmationToken confirmationToken =
                confirmationTokenDao.findByToken(token);
        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AccountInactiveException(
                    "Token expired, click resend " +
                            "new token to confirm your account.");
        }
        if (confirmationToken.getConfirmedAt() != null) {
            throw new AccountInactiveException("Email is already confirmed.");
        }
        // Update confirmation token, confirmedAt
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenDao.save(confirmationToken);
        return "Email is confirmed.";
    }


}
