package com.atm.business.concretes;

import com.atm.business.abstracts.ConfirmationTokenServices;
import com.atm.business.abstracts.EmailSenderServices;
import com.atm.business.abstracts.RoleServices;
import com.atm.business.abstracts.UserService;
import com.atm.core.bean.PasswordEncoderBean;
import com.atm.core.exceptions.EmailExistsException;
import com.atm.core.exceptions.PasswordMisMatchException;
import com.atm.core.utils.converter.DtoEntityConverter;
import com.atm.core.utils.strings_generators.SlugGenerator;
import com.atm.dao.daos.UserDao;
import com.atm.model.dtos.CustomUserDetailsDto;
import com.atm.model.dtos.UserDetailsDto;
import com.atm.model.dtos.UserDto;
import com.atm.model.entities.ConfirmationToken;
import com.atm.model.entities.Role;
import com.atm.model.entities.User;
import com.atm.core.utils.validators.UserNameExistsValidator;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
@Log4j2
public class UserManager implements UserService, UserDetailsService {
    private UserDao userDao;
    private DtoEntityConverter converter;
    private MessageServices messageServices;
    private ConfirmationTokenServices confirmationTokenServices;
    private RoleServices roleServices;
    private BCryptPasswordEncoder passwordEncoder;
    private EmailSenderServices emailSenderServices;
    private UserNameExistsValidator userNameExistsValidator;


    @SneakyThrows
    @Override
    public String save(UserDto userDto) {
        // Checking if username exists
        validateEmail(userDto.getEmail());
        // Converting userDto to User model
        User user = getUser(userDto);
        // Assign role to user
        assignRole(user);
        // Save the new user
        userDao.save(user);
        // Create and Save confirmation token
        String token = createAndSaveToken(user);
        // Send confirmation email
        sendEmail(token, user);

        return messageServices.getMessage("scs.user.signup");
    }

    // User private Helper orchestrated methods

    private void sendEmail(String token, User user) {
        String link = "http://localhost:8080/atm/user/verify?token=" + token;
        emailSenderServices.send(user.getEmail(),
                buildConfirmEmailBody(user.getFirstName() + " " + user.getLastName(),
                        link));
    }

    private String createAndSaveToken(User user) {
        ConfirmationToken confirmationToken = confirmationTokenServices.newConfirmationToken(user);
        confirmationTokenServices.saveConfirmationToken(confirmationToken);
        return confirmationToken.getToken();
    }

    private void assignRole(User user) {
        if (userDao.count() == 0)
            user.setRoles(List.of(roleServices.getOrCreateRole("ROLE_ADMIN")));
        else
            user.setRoles(List.of(roleServices.getOrCreateRole("ROLE_USER")));
    }

    private User getUser(UserDto userDto) {
        User user = (User) converter.dtoToEntity(userDto, new User());
        // Encrypting password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setEnabled(false);
        user.setAccountNonLocked(1);
        user.setSlug(new SlugGenerator().slug(userDto.getEmail()));
        return user;
    }

    private void validateEmail(String email) throws EmailExistsException {
        if (userNameExistsValidator.validate(email))
            throw new EmailExistsException(messageServices.getMessage("err.email.exists"));
    }

    private String buildConfirmEmailBody(String name, String link) {
        return "<p> Hi, " + name + ", </p>" +
                "<p>Thank you for registering with us," + "" +
                "Please, follow the link below to complete your registration.</p>" +
                "<a href=\"" + link + "\">Verify your email to activate your account</a>" +
                "<p> Thank you <br> Users Registration Portal Service";
    }

    // User private Helper orchestrated methods

    @Override
    public UserDto findByEmail(String email) {
        return (UserDto) converter.entityToDto(
                userDao.findByEmail(email), UserDto.class
        );
    }

    @Override
    public void resetPasswordSender(String email) {
        User user = userDao.findByEmail(email);
        // Create and save confirmation token
        String token = createAndSaveToken(user);
        String link = "http://localhost:8080/atm/user/reset?token=" + token;
        String message = "<p>Please, follow the link to reset your password." + "" +
                "</p>" +
                "<a href=\"" + link + "\">Reset Password</a>";
        emailSenderServices.send(user.getEmail(), message);
    }

    @Override
    public User findUserByToken(String token) {
        return userDao.findByEmail(
                confirmationTokenServices.findByToken(token).getEmail()
        );
    }

    @SneakyThrows
    @Override
    public String resetPassword(String password, String slug) {
        User user = userDao.findBySlug(slug).get();
        user.setPassword(PasswordEncoderBean.passwordEncoder().encode(password));
        userDao.save(user);
        return "Password reset successfully";
    }


    @SneakyThrows
    @Override
    public String update(UserDetailsDto userDto, String slug) {
        User user = userDao.findByEmail(userDto.getEmail());
        if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword()))
            throw new PasswordMisMatchException(
                    messageServices.getMessage("err.psd.mismatch"));

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        userDao.save(user);
        return messageServices.getMessage("scs.profile.update");
    }

    @Override
    // ResponseEntity<>
    public String delete(String slug) {
        Optional<User> user = userDao.findBySlug(slug);
        user.ifPresent(value -> userDao.delete(value));
        return "User deleted";
    }


    @Override
    public User findBySlug(String slug) {
        Optional<User> user = userDao.findBySlug(slug);
        if (user.isEmpty())
            throw new UsernameNotFoundException(messageServices.
                    getMessage("err.user.not.found"));
        return user.get();
    }


    @Override
    public List<UserDto> findAll() {
        return userDao.findAll()
                // convert to DTO using Stream API
                .stream().map(
                        user -> UserDto.builder()
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .email(user.getEmail())
                                .slug(user.getSlug())
                                .build()
                ).collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException(messageServices.getMessage("err.username.notfound"));
        }
        return new CustomUserDetailsDto(user);

    }

}
