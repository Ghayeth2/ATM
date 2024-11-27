package com.atm.business.concretes;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

// Messages services to load messages from properties files
@AllArgsConstructor
@Service
public class MessageServices {

    private MessageSource messageSource;

    // Hello + Auth Username (object) User
    public String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args,
                LocaleContextHolder.getLocale());
    }

    public String getMessage(String code) {
        return messageSource.getMessage(code,
                null, LocaleContextHolder.getLocale());
    }
}
