package com.atm.units.service;

import com.atm.business.abstracts.EmailSenderServices;
import com.atm.business.concretes.EmailSenderServicesManager;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class MailSenderManagerTest {

    @Mock
    private JavaMailSender mailSender;


    @InjectMocks
    private EmailSenderServicesManager emailSenderServices;

    @Test
    // should send an email
    void shouldSendAnEmail_WhenParametersAreGiven() throws MessagingException {
        // down bellow line will create Mocked MimeMessage not real one
//        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessage mimeMessage = new JavaMailSenderImpl().createMimeMessage();

        String to = "to";
        String email = "email";
        when(mailSender.createMimeMessage())
                .thenReturn(mimeMessage);
        emailSenderServices.send(to, email);
        ArgumentCaptor<MimeMessage> captor =
                ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());
        MimeMessage capturedMessage = captor.getValue();
        Assertions.assertThat("Confirm your email")
                .isEqualTo(capturedMessage.getSubject());
    }

    // Testing if it throws exception if something goes wrong
    @Test
    void itShouldThrowMailSendingException_WhenSomeThingGoesWrong() {
        MimeMessage mimeMessage = new JavaMailSenderImpl().createMimeMessage();
        when(mailSender.createMimeMessage())
                .thenThrow(new NullPointerException("Null pointer exception"));
        NullPointerException ex = org.junit.jupiter.api
                .Assertions.assertThrows(
                        NullPointerException.class,
                        () -> emailSenderServices.send("to", "email")
                );
        Assertions.assertThat(ex.getMessage())
                .isEqualTo("Null pointer exception");
    }
}
