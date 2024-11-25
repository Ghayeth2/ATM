package com.atm.units.service;

import com.atm.business.abstracts.ConfirmationTokenServices;
import com.atm.business.abstracts.EmailSenderServices;
import com.atm.business.abstracts.RoleServices;
import com.atm.business.abstracts.UserService;
import com.atm.business.concretes.MessageServices;
import com.atm.business.concretes.UserManager;
import com.atm.core.bean.PasswordEncoderBean;
import com.atm.core.exceptions.EmailExistsException;
import com.atm.core.exceptions.PasswordMisMatchException;
import com.atm.core.utils.converter.DtoEntityConverter;
import com.atm.core.utils.validators.UserNameExistsValidator;
import com.atm.dao.daos.UserDao;
import com.atm.model.dtos.CustomUserDetailsDto;
import com.atm.model.dtos.UserDetailsDto;
import com.atm.model.dtos.UserDto;
import com.atm.model.entities.ConfirmationToken;
import com.atm.model.entities.Role;
import com.atm.model.entities.User;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@Log4j2

public class UserManagerTest {

    @Mock
    private UserDao userDao;

    @Mock
    private UserNameExistsValidator userNameValidator;

    @Mock
    private RoleServices roleServices;

    @Mock
    private DtoEntityConverter converter;

    @Mock
    private MessageServices messageServices;

    @Mock
    private ConfirmationTokenServices cServices;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private EmailSenderServices emailServices;

    @InjectMocks
    private UserManager userManager;

    // Testing UserNameExistsValidator is working
    @Test
    void shouldThrowUserExistsException_WhenExistedEmailIsProvided() {
        // Arrange - only need the email to test throwing exception
        UserDto userDto = UserDto.builder()
                .email("test@gmail.com").build();
        // When userNameValidator is called Return that it exists
        when(userNameValidator.validate("test@gmail.com"))
                .thenReturn(true);
        // When the messageServices is extracting errMsg, return my msg
        // So that I can expect what the message will be => assert the test
        when(messageServices.getMessage("err.email.exists"))
                .thenReturn("Username already exists");
        // Invoke & throw my expected exception
        // Calling my service method, assuring exception is thrown
        EmailExistsException ex = Assertions.
                assertThrows(EmailExistsException.class, () -> {
            userManager.save(userDto);
        });
        // Assert the result
        Assertions.assertEquals("Username already exists",
                ex.getMessage());
    }

    private String mailBody(String name, String link) {
        return "<p> Hi, "+ name + ", </p>"+
                "<p>Thank you for registering with us,"+"" +
                "Please, follow the link below to complete your registration.</p>"+
                "<a href=\"" + link + "\">Verify your email to activate your account</a>"+
                "<p> Thank you <br> Users Registration Portal Service";
    }

    /*
        Ok, so can't test doesn't throw for userManager.save()
        cuz, it won't throw & will continue with userManager.save()
        Meaning: Exact case as testing userManager.save()
     */
    @Test
    void shouldSaveUser_WhenUserDtoIsProvided() {
        // Arrange fake userDto data with only Email
        UserDto userDto = UserDto.builder()
                .email("test@gmail.com").firstName("first")
                .lastName("last").password("pass").build();
        // Arranging fake user model
        User user = User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .enabled(false).accountNonLocked(1)
                .build();
        user.setSlug("slug");
        // When calling validate email
        when(userNameValidator.validate(userDto.getEmail())).thenReturn(false);
        when(converter.dtoToEntity(userDto, new User()))
                .thenReturn(
                        user
                );
        when(PasswordEncoderBean.passwordEncoder())
                .thenReturn(new BCryptPasswordEncoder());
        // When checking for any past users
        when(userDao.count()).thenReturn(0L);
        // When get or creating Role
        when(roleServices.getOrCreateRole("ROLE_ADMIN"))
                .thenReturn(new Role("ROLE_ADMIN"));
        // I am not expecting any response from userDao after saving the entity
        when(userDao.save(any(User.class))).thenReturn(user);
        ConfirmationToken token = ConfirmationToken.builder()
                .token("token").email(user.getEmail()).build();
        when(cServices.newConfirmationToken(any(User.class)))
                .thenReturn(token);
        // Setting the link for mocking
        // TODO: Note:
        /*
            When testing mock service, I should do as the actual service
            do & expect. Down bellow, my actual service (mail sending)
            was expecting mail-message with link + token.

            I did create the token, but missed sending it to mock service
            which ran the actual service with (NULL) for token. Therefore,
            there was a mismatch in arguments. In Mocking I used "token"
            as a value of ConfirmationToken, but when called the userSave()
            I did not send a token, so it went as NULL.
         */
        String link = "http://localhost:8080/atm/user/verify?token=" + token.getToken();
        doNothing().when(cServices).saveConfirmationToken(any(ConfirmationToken.class));
        doNothing().when(emailServices).send(user.getEmail()
                , mailBody(
                        user.getFirstName()+" "+user.getLastName(),
                        link
                ));
        // No err-msg will be returned
        // Asserting no exception was thrown
        // Executable >> Interface >> Lambda
        String result = userManager.save(userDto);
        Assertions.assertEquals(token.getToken(), result);
    }

    // findByEmail
    @Test
    void shouldReturnUser_WhenEmailIsSent() {
        // Arrange
        User user = User.builder()
                .firstName("first").lastName("last")
                .password("pass").email("test@gmail.com")
                .build();
        UserDto userDto = UserDto.builder().email("test@gmail.com")
                .firstName("first")
                .lastName("last").password("pass").build();
        // Act
        when(userDao.findByEmail(user.getEmail())).thenReturn(user);
        when(converter.entityToDto(user,
                // in UserManager's findByEmail() I used direct calling
                // with parameters similar to ones here, that's why
                // when i changed it to "new UserDto()" it kept failing
                UserDto.class)).thenReturn(userDto);
        // Calling the service
        UserDto res = userManager.findByEmail(user.getEmail());
        // Assert result
        Assertions.assertEquals(userDto, res);
    }

    // Testing resetPasswordSender (mail sender)
    @Test
    void shouldSendMail_WhenPasswordResettingIsWanted() {
        // Arrange
        String email = "test@gmail.com";
        User user = User.builder().firstName("first")
                .lastName("last").email(email).build();
        ConfirmationToken token = ConfirmationToken.builder()
                .email(user.getEmail()).token("token").build();
        String link = "http://localhost:8080/atm/user/reset?token=" + token
                .getToken();
        String message = "<p>Please, follow the link to reset your password."+"" +
                "</p>"+
                "<a href=\"" + link + "\">Reset Password</a>";
        // Act
        when(userDao.findByEmail(any())).thenReturn(user);
        when(cServices.newConfirmationToken(any(User.class)))
                .thenReturn(token);
        doNothing().when(cServices).saveConfirmationToken(any(ConfirmationToken.class));
        doNothing().when(emailServices).send(user.getEmail(), message);
        userManager.resetPasswordSender(email);
        // Assert
        // Using the Mockito Framework's verify() method
        // That assures the target method was triggerred
        // Along with that, we can use ArgumentsMatchers
        // To check whether the arguments are the same or not
        Mockito.verify(emailServices)
                .send(
                        ArgumentMatchers.eq(user.getEmail()),
                        ArgumentMatchers.contains(message)
                );
    }

    // Testing findUserByToken
    @Test
    void shouldFindUser_WhenATokenIsGiven() {
        // Arrange

        String email = "test@gmail.com";
        User user = User.builder().firstName("first")
                .lastName("last").email(email).build();
        ConfirmationToken token = ConfirmationToken.builder()
                .token("token").email(email).build();
        // Act
        when(userDao.findByEmail(any())).thenReturn(user);
        when(cServices.findByToken(token.getToken())
                ).thenReturn(token);
        // Calling the actual service
        User res = userManager.findUserByToken(token.getToken());
        // Assert
        Assertions.assertEquals(email, res.getEmail());
    }

    // should throw password mismatch exception
    @Test
    void shouldThrowPasswordMisMatchException_WhenWrongPasswordIsGiven() {
        User user = User.builder().email("test@gmail.com")
                .firstName("name").password("pass").build();
        UserDetailsDto userDto = UserDetailsDto.builder().email(user.getEmail())
                        .firstName(user.getFirstName()).password(user.getPassword())
                        .build();
        when(userDao.findByEmail(user.getEmail())).thenReturn(user);
//        when(passwordEncoder.matches(any(), any())).thenReturn(false);
        when(messageServices.getMessage(any())).thenReturn("Password mismatched!");
        // It handles the job of triggering the code where exception is thrown
        PasswordMisMatchException ex = Assertions.assertThrows(
                PasswordMisMatchException.class,
                () -> userManager.update(userDto, any())
        );
        Assertions.assertEquals("Password mismatched!", ex.getMessage());
    }

    // Update a user
    @Test
    void shouldUpdateUser_WhenUserAndSlugIsProvided() throws PasswordMisMatchException {
        UserDetailsDto userDto = UserDetailsDto.builder().firstName("first")
                .lastName("last").password("pass").email("email").build();
        User user = User.builder().firstName(userDto.getFirstName())
                .password(userDto.getPassword())
                .lastName(userDto.getLastName()).email(userDto.getEmail()).build();
        when(userDao.findByEmail(any())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        String res = userManager.update(userDto, anyString());
        Assertions.assertEquals(" your details are updated", res);
    }

    // Delete user test
    @Test
    void shouldDeleteAUser_WhenSlugIsProvided() {
        User user = User.builder().password("pass")
                .firstName("first").lastName("last")
                .email("test@gmail.com").build();
        when(userDao.findBySlug(anyString())).thenReturn(Optional
                .of(user));
        doNothing().when(userDao).delete(user);
        String res = userManager.delete(anyString());
        Assertions.assertEquals("User deleted", res);
    }

    // returning UsernameNotFoundException test
    @Test
    void shouldThrowUsernameNotFoundException_WhenSlugDoesNotMatchAny() {
        /*
        Optional class is designed to handle Null pointer exception internally
        The way of doing it, is to hold an Empty value in case no data was returned
         */
        when(userDao.findBySlug(anyString())).thenReturn(Optional.empty());
        when(messageServices.getMessage(anyString()))
                .thenReturn("Username not found!");
        UsernameNotFoundException ex = Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> userManager.findBySlug(anyString())
        );
        Assertions.assertEquals("Username not found!", ex.getMessage());
    }

    // FindBySlug
    @Test
    void shouldFindAUser_WhenSlugIsProvided() {
        User user = User.builder().email("test@gmail.com")
                .password("pass").build();
        when(userDao.findBySlug(anyString())).thenReturn(Optional.of(user));
        User res = userManager.findBySlug(anyString());
        Assertions.assertEquals(user, res);
    }

    // FindAll
    @Test
    void shouldFindAll_WhenMethodIsInvoked() {
        User user = User.builder().email("ea@gmail.com").password("pass").build();
        List<User> users = List.of(user);
        when(userDao.findAll()).thenReturn(users);
        List<UserDto> res = userManager.findAll();
        Assertions.assertEquals(1, res.size());
    }

    // LoadByUserName
    @Test
    void shouldLoadUser_WhenEmailIsGiven() {
        User user = User.builder().email("email@gmail.com")
                .password("pass").firstName("first").lastName("last").build();
        CustomUserDetailsDto userDto = CustomUserDetailsDto.builder()
                .user(user).build();
        when(userDao.findByEmail(anyString())).thenReturn(user);
        UserDetails res = userManager.loadUserByUsername(anyString());
        Assertions.assertEquals(
                user.getFirstName() + " " + user.getLastName(),
                res.getUsername()
        );
    }

    // Testing resetPassword
    @Test
    void shouldResetPassword_WhenItIsInvoked() {
        // Arrange
        User user = User.builder().email("test@gmail.com")
                .password("pass").build();
        // Act
        when(userDao.findBySlug(any())).thenReturn(Optional.ofNullable(user));
        // Unnecessary code
//        when(passwordEncoder.encode(any())).thenReturn("pass");
        when(userDao.save(any(User.class))).thenReturn(user);
        // Calling the service
        String res = userManager.resetPassword("pass", "slug");
        // Assert
        Assertions.assertEquals("Password reset successfully", res);
    }
}
