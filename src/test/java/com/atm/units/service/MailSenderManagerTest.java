package com.atm.units.service;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class MailSenderManagerTest {

    @Mock
    private JavaMailSender mailSender;

    // should send an email
    void shouldSendAnEmail_WhenParametersAreGiven() {

    }
}
