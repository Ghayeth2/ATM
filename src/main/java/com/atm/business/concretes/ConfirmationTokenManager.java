package com.atm.business.concretes;

import com.atm.business.abstracts.ConfirmationTokenServices;
import com.atm.business.abstracts.UserAccountServices;
import com.atm.core.exceptions.AccountInactiveException;
import com.atm.dao.ConfirmationTokenDao;
import com.atm.dao.ConfirmationTokenDaoImpl;
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
    private final UserAccountServices userAccountServices;
    @Value("${verification.code.expiration}")
    int verificationCodeExpiration;

    public ConfirmationTokenManager(ConfirmationTokenDao confirmationTokenDao,
                                    UserAccountServices userAccountServices) {
        this.confirmationTokenDao = confirmationTokenDao;
        this.userAccountServices = userAccountServices;
    }

    @Override
    public void saveConfirmationToken(ConfirmationToken confirmationToken) {
        log.info("Token : "+ confirmationToken.getToken()+" "+confirmationToken.getEmail());
        confirmationTokenDao.save(confirmationToken);
    }

    @Override
    public ConfirmationToken newConfirmationToken(User user) {
        String token = UUID.randomUUID().toString();
        return ConfirmationToken.builder()
                .token(token)
                .expiresAt(LocalDateTime.now().plusMinutes(verificationCodeExpiration))
                .email(user.getEmail())
                .build();
    }

    @Override
    public ConfirmationToken findByToken(String token) {
        return confirmationTokenDao.findByToken(token);
    }

    @Override
    public boolean isTokenValid(String token) {
        log.info("Token : "+ token + " is valid: "+confirmationTokenDao.isExpired(token));
        ConfirmationToken cToken = confirmationTokenDao.findByToken(token);
        if (cToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }
        return true;
    }


    @Override
    public String  confirmToken(String token) throws AccountInactiveException {
        // TODO: Confirm token
        ConfirmationToken confirmationToken = confirmationTokenDao.findByToken(token);
        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AccountInactiveException("Token expired, click resend new token to confirm your account.");
        }
        if (confirmationToken.getConfirmedAt() != null) {
            throw new AccountInactiveException("Email is already confirmed.");
        }
        // Update confirmation token, confirmedAt
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenDao.save(confirmationToken);
        // TODO: Enable user's account
        User user = userAccountServices.findByEmail(confirmationToken.getEmail());
        user.setEnabled(true);
        userAccountServices.activateAccount(user);
        return "confirmed";
    }


}
