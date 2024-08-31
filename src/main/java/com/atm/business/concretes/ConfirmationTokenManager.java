package com.atm.business.concretes;

import com.atm.business.abstracts.ConfirmationTokenServices;
import com.atm.business.abstracts.UserAccountServices;
import com.atm.core.exceptions.AccountInactiveException;
import com.atm.dao.ConfirmationTokenDao;
import com.atm.model.entities.ConfirmationToken;
import com.atm.model.entities.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
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
        confirmationTokenDao.save(confirmationToken);
    }

    @Override
    public ConfirmationToken newConfirmationToken(User user) {

        String token = UUID.randomUUID().toString();
        return ConfirmationToken.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                // Minutes has to come from configuration file
                .expiredAt(LocalDateTime.now().plusMinutes(verificationCodeExpiration))
                .user(user)
                .build();
    }

    @Override
    public ConfirmationToken findConfirmationToken(String confirmationToken) {
        return confirmationTokenDao.findByToken(confirmationToken).orElseThrow(
                () -> new IllegalArgumentException("Confirmation token not found")
        );
    }


    @Override
    public boolean isTokenValid(String confirmationToken) {
        Optional<ConfirmationToken> token = confirmationTokenDao.findByToken(confirmationToken);
        log.info("token"+ token.get().getUser().getEmail());
        log.info("is present: "+token.isPresent());
        if (token.isPresent()) {
            if (token.get().getExpiredAt().isAfter(LocalDateTime.now())) {
                log.info("valid token");
                return true;
            }
        }
        return false;
    }

    @Override
    public String  confirmToken(String token) throws AccountInactiveException {
        // TODO: Confirm token
        ConfirmationToken confirmationToken = confirmationTokenDao.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("No token found!"));

        if (confirmationToken.getConfirmedAt() != null)
            throw new AccountInactiveException("Email is already confirmed!");

        LocalDateTime expiresAt = confirmationToken.getExpiredAt();
        if (expiresAt.isBefore(LocalDateTime.now())) {
            // TODO: resend confirmation token (create new one)
            confirmationTokenDao.save(newConfirmationToken(
                    confirmationToken.getUser()
            ));
            // TODO: Resend confirmation email
            throw new AccountInactiveException("Token is already expired, check your account" +
                    " for new token just been sent..");
        }
        // Update confirmation token, confirmedAt
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenDao.save(confirmationToken);
        // TODO: Enable user's account
        User user = confirmationToken.getUser();
        user.setEnabled(true);
        userAccountServices.activateAccount(user);
        return "confirmed";
    }


}
