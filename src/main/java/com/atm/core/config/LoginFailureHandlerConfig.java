package com.atm.core.config;

import com.atm.business.abstracts.UserAccount;
import com.atm.business.concretes.MessageServices;
import com.atm.business.concretes.UserAccountManager;
import com.atm.core.exceptions.AccountInactiveException;
import com.atm.model.dtos.CustomUserDetailsDto;
import com.atm.model.entities.User;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
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
    private final MessageServices messageServices;
    // Do i need to store redirecting routes in separated file?? properties??
    // yes i do, in case of base routes changes (/atm/login >> /atm/v*/login)
    private String BASE_URL;
    private String FAILURE_URL;
//    private String ACCOUNT_INACTIVE_URL = "/atm/login?inactive";

    public LoginFailureHandlerConfig(UserAccount userAccount, MessageServices messageServices) {
        this.userAccount = userAccount;
        this.messageServices = messageServices;
    }

    @PostConstruct
    public void init() {
        BASE_URL = messageServices.getMessage("route.login.failure");
    }


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("username");
        User user = userAccount.findByEmail(email);
        if (user != null) {
            if (!user.isEnabled()){
                response.sendRedirect(BASE_URL+"inactive");
                return;
            }
            if (user.getAccountNonLocked() == 1) {
                if (user.getFailedAttempts() < UserAccountManager.MAX_FAILED_ATTEMPTS - 1) {
                    userAccount.increaseFailedAttempts(user);
                } else {
                    userAccount.lock(user);
                    response.sendRedirect(BASE_URL+"locked");
                }
            } else {
                if (userAccount.unlockWhenTimeExpired(user)) {
                    // those messages should be dynamically sat
                    response.sendRedirect(BASE_URL+"unlocked");
                }
            }
        } else {
            // No user found (sign up required)
            response.sendRedirect(BASE_URL+"notFound");
            return;
        }

        // Default (username or password invalidation)
        setDefaultFailureUrl(BASE_URL+"failure");
        super.onAuthenticationFailure(request, response, exception);
    }

}

