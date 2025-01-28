package com.atm.business.concretes;

import com.atm.business.abstracts.EmailServices;
import com.atm.model.dtos.EmailDetails;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class EmailManager implements EmailServices {
    @Value("${sendgrid.secret.key}")
    private String secretKey;

    @Override @SneakyThrows
    public void sendEmail(EmailDetails emailDetails) {
        // From email
        Email fromEmail = setEmail(emailDetails.getFromAddress()
                .getEmailAddress(), emailDetails.getFromAddress()
                .getName());
        // To email
        Email toEmail = setEmail(emailDetails.getToAddress()
                .getEmailAddress(), emailDetails.getToAddress()
                .getName());
        // Setting up content
        Content content = new Content("text/html", emailDetails.getBody());
        // Setting up Mail
        Mail mail = new Mail(fromEmail, emailDetails.getSubject(), toEmail, content);
        // Preparing request (mail) body
        Request request = new Request();
        request.setEndpoint("mail/send"); // The SendGrid API endpoint
        request.setMethod(Method.POST); // Post method will encrypt mail
        request.setBody(mail.build());
        // Initializing SendGrid
        SendGrid sendGrid = new SendGrid(secretKey);
        // Calling API and sending request
        Response response = sendGrid.api(request);
        log.info("{} {}", response.getBody(), response.getStatusCode());
    }

    private Email setEmail(String emailAddress, String name) {
        Email email = new Email();
        email.setEmail(emailAddress);
        email.setName(name);
        return email;
    }
}
