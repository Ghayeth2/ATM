package com.atm.business.concretes;

import com.atm.business.abstracts.ConfirmationTokenServices;
import com.atm.business.abstracts.EmailSenderServices;
import com.atm.business.abstracts.TempUserServices;
import com.atm.core.exceptions.EmailExistsException;
import com.atm.core.utils.validators.UserNameExistsValidator;
import com.atm.dao.daos.TempUserDao;
import com.atm.model.dtos.TempUser;
import com.atm.model.dtos.UserDto;
import com.atm.model.entities.ConfirmationToken;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TempUserManager implements TempUserServices {
    private final TempUserDao tempUserDao;
    private final ConfirmationTokenServices confirmationTokenServices;
    private final MessageServices messageServices;
    private final EmailSenderServices emailSenderServices;
    private final UserNameExistsValidator userNameExistsValidator;

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
        sendEmail(token, user);

        return messageServices.getMessage("scs.user.signup");
    }

    // User private Helper orchestrated methods

    private void sendEmail(String token, TempUser user) {
        String link = "http://localhost:8080/atm/user/verify?token=" + token;
        emailSenderServices.send(user.getEmail(),
                buildConfirmEmailBody(user.getFirstName()
                                + " " + user.getLastName(),
                        link));
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
