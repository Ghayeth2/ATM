package com.atm.units.service;
import com.atm.business.concretes.MessageServices;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class MessageServicesTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private MessageServices messageServices;

    @Test
    void shouldGetMessage_WhenMessageCodeIsSent() {
        String message = "message";
        when(messageSource.getMessage(anyString(), any(),
                any(Locale.class))).thenReturn(message);
        String res = messageServices.getMessage("code");
        Assertions.assertThat(res).isEqualTo(message);
    }

    @Test
    void shouldGetMessage_WhenCodeAndArgsAreSent() {
        String message = "message";
        Object[] args = new Object[]{"arg1", "arg2"};
        /*
        The exception of having invalid matchers args
        is generated when using any() in a method just for
        some args than all. And also when calling the actual
        service we need to call it with test data rather than
        mocks
         */
        when(messageSource.getMessage(anyString(),
                any(), any(Locale.class))).thenReturn(message);
        String res = messageServices.getMessage("code", args);
        Assertions.assertThat(res).isEqualTo(message);
    }
}
