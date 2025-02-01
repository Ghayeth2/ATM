package com.atm.business.concretes;

import com.atm.business.abstracts.ConfirmationTokenServices;
import com.atm.business.abstracts.EmailServices;
import com.atm.business.abstracts.TempUserServices;
import com.atm.core.exceptions.EmailExistsException;
import com.atm.core.utils.strings_generators.EmailContentBuilder;
import com.atm.core.utils.validators.UserNameExistsValidator;
import com.atm.dao.daos.TempUserDao;
import com.atm.model.dtos.EmailDetails;
import com.atm.model.dtos.EmailInfo;
import com.atm.model.dtos.TempUser;
import com.atm.model.dtos.UserDto;
import com.atm.model.entities.ConfirmationToken;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TempUserManager implements TempUserServices {
    private final TempUserDao tempUserDao;
    private final ConfirmationTokenServices confirmationTokenServices;
    private final MessageServices messageServices;
    private final EmailServices emailServices;
    private final UserNameExistsValidator userNameExistsValidator;

    @Autowired
    public TempUserManager(TempUserDao tempUserDao,
                           ConfirmationTokenServices confirmationTokenServices,
                           MessageServices messageServices,
                           EmailServices emailServices,
                           UserNameExistsValidator userNameExistsValidator) {
        this.tempUserDao = tempUserDao;
        this.confirmationTokenServices = confirmationTokenServices;
        this.messageServices = messageServices;
        this.emailServices = emailServices;
        this.userNameExistsValidator = userNameExistsValidator;
    }

    @Value("${server.port}")
    private int port;

    @Value("${server.local.host}")
    private String localHost;

    @Value("${server.remote.host}")
    private String remoteHost;

    @SneakyThrows
    @Override
    public String save(UserDto userDto) {
        // Checking if username exists
        validateEmail(userDto.getEmail());
        // Converting userDto to User model
        TempUser user = getUser(userDto);
        // Save the new user
        tempUserDao.save(user);
        // Create and Save confirmation token
        String token = createAndSaveToken(user.getEmail());
        // Send confirmation email
        handleVerificationMailSending(token, user);

        return messageServices.getMessage("scs.user.signup");
    }

    // User private Helper orchestrated methods
    // should be name relates to this class
    private void handleVerificationMailSending(String token, TempUser user) {
        // When moving to Production environment, the link's host will be atmsemu.net/
        String link = "http://"+localHost+":"+port+"/atm/user/verify?token=" + token;
        // Subject of the email
        String subject = "Verify your email address";
        String content = new EmailContentBuilder().buildBody(
                user.getFirstName(),
                subject, link
        );
        System.out.println("Content of email: " + content);
        // Building EmailDetails & EmailInfo
        EmailInfo fromAddress = EmailInfo.builder()
                .emailAddress("ghayeth.msri@gmail.com").name("ATM simulator").build();
        EmailInfo toAddress = EmailInfo.builder()
                .emailAddress(user.getEmail()).name(user.getFirstName()
                        + " " + user.getLastName()).build();
        EmailDetails emailDetails = EmailDetails.builder()
                .fromAddress(fromAddress).toAddress(toAddress)
                .subject(subject)
                .body(content).build();
        // Sending email request
        emailServices.sendEmail(emailDetails);
    }

    private String createAndSaveToken(String email) {
        ConfirmationToken confirmationToken = confirmationTokenServices
                .newConfirmationToken(email);
        confirmationTokenServices.saveConfirmationToken(confirmationToken);
        return confirmationToken.getToken();
    }

    private TempUser getUser(UserDto userDto) {
        return TempUser.builder()
                .notConfirmed(true)
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .password(userDto.getPassword()).build();
    }

    private void validateEmail(String email) throws EmailExistsException {
        if (userNameExistsValidator.validate(email))
            throw new EmailExistsException(messageServices
                    .getMessage("err.email.exists"));
    }

    private String buildConfirmEmailBody(String name, String link) {
        return "<p> Hi, " + name + ", </p>" +
                "<p>Thank you for registering with us," + "" +
                "Please, follow the link below to complete your registration.</p>" +
                "<a href=\"" + link + "\">Verify your email to activate your account</a>" +
                "<p> Thank you <br> Users Registration Portal Service";
    }

    @Override
    public TempUser findByToken(String token) {
        return findByUsername(
                confirmationTokenServices
                        .findByToken(token)
                        .getEmail()
        ).get();
    }

    @Override
    public void updateNotConfirmed(String email) {
        System.out.println("Email for updating notConfirmed field: " + email);
        tempUserDao.updateNotConfirmed(email);
    }

    @Override
    public Optional<TempUser> findByUsername(String username) {
        return tempUserDao.findByEmail(username);
    }
}
