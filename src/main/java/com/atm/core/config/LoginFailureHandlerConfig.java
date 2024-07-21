package com.atm.core.config;

import com.atm.business.abstracts.UserAccount;
import com.atm.business.concretes.MessageServices;
import com.atm.business.concretes.UserAccountManager;
import com.atm.model.dtos.CustomUserDetailsDto;
import com.atm.model.entities.User;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
@Log4j2
public class LoginFailureHandlerConfig extends SimpleUrlAuthenticationFailureHandler {

    private final UserAccount userAccount;
    private MessageServices messageServices;
    private final String FAILURE_URL = "/atm/login?failure";
    private final String NOT_FOUND_URL = "/atm/login?notFound";

    public LoginFailureHandlerConfig(UserAccount userAccount, MessageServices messageServices) {
        this.userAccount = userAccount;
        this.messageServices = messageServices;
        setDefaultFailureUrl(FAILURE_URL);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("username");
        User user = userAccount.findByEmail(email);
        if (user != null) {
            handleExistingUser(user);
        } else {
            response.sendRedirect(NOT_FOUND_URL);
            return;
        }

        super.onAuthenticationFailure(request, response, exception);
    }

    private void handleExistingUser(User user) {
        if (user.getAccountNonLocked() == 1) {
            if (user.getFailedAttempts() < UserAccountManager.MAX_FAILED_ATTEMPTS - 1) {
                userAccount.increaseFailedAttempts(user);
            } else {
                userAccount.lock(user);
                throw new LockedException(messageServices.getMessage("err.account.locked"));
            }
        } else {
            if (userAccount.unlockWhenTimeExpired(user)) {
                // those messages should be dynamically sat
                throw new LockedException(messageServices.getMessage("err.account.unlocked"));
            }
        }
    }
}

