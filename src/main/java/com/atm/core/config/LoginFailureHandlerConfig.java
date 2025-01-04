package com.atm.core.config;

import com.atm.business.abstracts.TempUserServices;
import com.atm.business.abstracts.UserAccountServices;
import com.atm.business.abstracts.UserService;
import com.atm.business.concretes.MessageServices;
import com.atm.business.concretes.UserAccountServicesManager;
import com.atm.model.dtos.TempUser;
import com.atm.model.entities.User;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@Component
@Log4j2
public class LoginFailureHandlerConfig extends SimpleUrlAuthenticationFailureHandler {

    private final UserAccountServices userAccountServices;
    private final MessageServices messageServices;
    private final TempUserServices tempUserServices;
    // Do i need to store redirecting routes in separated file?? properties??
    // yes i do, in case of base routes changes (/atm/login >> /atm/v*/login)
    private String BASE_URL;

    private String FAILURE_URL;
//    private String ACCOUNT_INACTIVE_URL = "/atm/login?inactive";

    public LoginFailureHandlerConfig(UserAccountServices userAccountServices,
                                     MessageServices messageServices,
                                     TempUserServices tempUserServices) {
        this.userAccountServices = userAccountServices;
        this.messageServices = messageServices;
        this.tempUserServices = tempUserServices;

    }

    @PostConstruct
    public void init() {
        BASE_URL = messageServices.getMessage("route.login.failure");
    }


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("username");
        User user = userAccountServices.findByEmail(email);
        Optional<TempUser> temp = tempUserServices.findByUsername(email);
        if (temp.isPresent()) {
            if (temp.get().isNotConfirmed()){
                response.sendRedirect(BASE_URL+"inactive");
                return;
            }
        }
        if (user != null) {
            if (user.getAccountNonLocked() == 1) {
                if (user.getFailedAttempts() < UserAccountServicesManager.MAX_FAILED_ATTEMPTS - 1) {
                    userAccountServices.increaseFailedAttempts(user);
                } else {
                    userAccountServices.lock(user);
                    response.sendRedirect(BASE_URL+"locked");
                }
            } else {
                if (userAccountServices.unlockWhenTimeExpired(user)) {
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

