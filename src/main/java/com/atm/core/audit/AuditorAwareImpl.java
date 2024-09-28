package com.atm.core.audit;

import com.atm.model.dtos.CustomUserDetailsDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
@Log4j2
// Auditor config // getting logged in username
// Auditing is not used for getting logged in username
// it is used to keep tracking who did what in the system
// after they are logged in.
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
