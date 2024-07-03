package com.atm.core.config;

import com.atm.business.abstracts.UserAccount;
import com.atm.business.concretes.UserAccountManager;
import com.atm.model.dtos.CustomUserDetailsDto;
import com.atm.model.entities.User;
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

@Component @Log4j2
public class LoginFailureHandlerConfig extends SimpleUrlAuthenticationFailureHandler {

    private UserAccount userAccount;

    @Autowired
    public LoginFailureHandlerConfig(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("username");
        User user = userAccount.findByEmail(email);
        if (user != null) {
            if (user.getAccountNonLocked() == 1) {
                if (user.getFailedAttempts() < UserAccountManager.MAX_FAILED_ATTEMPTS - 1) {
                    userAccount.increaseFailedAttempts(user);
                } else {
                    userAccount.lock(user);
                    exception = new LockedException("Your account has been locked due to 3 failed attempts."
                            + " It will be unlocked after 24 hours.");
                }
            } else {
                if (userAccount.unlockWhenTimeExpired(user)) {
                    exception = new LockedException("Your account has been unlocked. Please try to login again.");
                    super.setDefaultFailureUrl("/atm/login?failure");
                    super.onAuthenticationFailure(request, response, exception);
                }
            }

        }else{
            response.sendRedirect("/atm/login?notFound");
            return;
        }


        super.setDefaultFailureUrl("/atm/login?failure");
        super.onAuthenticationFailure(request, response, exception);
    }
}
