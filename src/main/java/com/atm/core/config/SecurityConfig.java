package com.atm.core.config;


import com.atm.business.abstracts.UserAccount;
import com.atm.core.bean.DaoAuthenticationProviderBean;
import com.atm.core.bean.LoginSuccessHandlerBean;
import com.atm.core.bean.PasswordEncoderBean;
import com.atm.model.dtos.CustomUserDetailsDto;
import com.atm.model.entities.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.beans.JavaBean;
import java.io.IOException;

@Configuration
@Log4j2
@EnableWebSecurity
public class SecurityConfig  {
    private final LoginSuccessHandlerConfig loginSuccessHandler;
    private final DaoAuthenticationProviderBean authenticationProvider;
    private final LoginFailureHandlerConfig loginFailureHandler;

    @Autowired
    public SecurityConfig(
            LoginSuccessHandlerConfig loginSuccessHandler,
            LoginFailureHandlerConfig loginFailureHandler,
            DaoAuthenticationProviderBean auth
    ) {
        this.loginSuccessHandler = loginSuccessHandler;
        this.loginFailureHandler = loginFailureHandler;
        this.authenticationProvider = auth;
    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                /*
                CSRF is for Client-Site Request Forgery
                it is enabled by default for protecting this type of attacks
                where attacker may use client's info for unwanted actions in a site
                that the user is registered or logged in.
                If the application is running on mobile or other services than web,
                then no need to enable this feature since the attacks are only on Web
                Also, if the application is a stateless without keep tracking user details
                we can again disable this feature.

                Best practice is to leave it enabled
                 */
//                .csrf(csrf -> csrf.disable())
                .authorizeRequests()
                .requestMatchers(
                        // The problem was from the security chain. I missed mentioning POST URL
                        "/atm/registration", "/atm/registration?notMatched",
                        "/atm/registration?success","/atm/user/**","/atm", "/assets/js/**",
                        "/assets/css/**", "/assets/img/**", "/assets/fonts/**",
                        "/assets/modules/**", "/public/**", "/atm/login", "/atm/email_confirmed"
                ).permitAll()
                .anyRequest().authenticated()
                .and()

                .formLogin(form ->
                        form.loginPage("/atm/login")
                                .permitAll()
                                .successHandler(loginSuccessHandler)
                                .failureHandler(loginFailureHandler)
                )
                .logout(logout ->
                        logout.invalidateHttpSession(true)
                                .clearAuthentication(true)
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")
                                )
                                .logoutSuccessUrl("/atm/login?logout")
                                .permitAll()

                );

        http.authenticationProvider(authenticationProvider.authenticationProvider());


        return http.build();
    }
}
