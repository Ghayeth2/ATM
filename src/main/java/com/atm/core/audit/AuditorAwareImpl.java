package com.atm.core.audit;

import com.atm.model.dtos.CustomUserDetailsDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
@Log4j2
public class AuditorAwareImpl implements AuditorAware {

    CustomUserDetailsDto userDetailsDto;

    @Override
    public Optional getCurrentAuditor() {
        Object principal = new Object();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null)
             principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return Optional.of(((UserDetails)principal).getUsername());
        } else {
            return Optional.of(principal.toString());
        }
    }
}
