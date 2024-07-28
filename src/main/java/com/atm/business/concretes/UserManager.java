package com.atm.business.concretes;

import com.atm.business.abstracts.ConfirmationTokenServices;
import com.atm.business.abstracts.EmailSender;
import com.atm.business.abstracts.UserService;
import com.atm.core.bean.PasswordEncoderBean;
import com.atm.core.exceptions.AccountInactiveException;
import com.atm.core.exceptions.EmailExistsException;
import com.atm.core.exceptions.PasswordMisMatchException;
import com.atm.core.utils.converter.DtoEntityConverter;
import com.atm.core.utils.stringsOPS.SlugGenerator;
import com.atm.dao.ConfirmationTokenDao;
import com.atm.dao.RoleDao;
import com.atm.dao.UserDao;
import com.atm.model.dtos.CustomUserDetailsDto;
import com.atm.model.dtos.UserDetailsDto;
import com.atm.model.dtos.UserDto;
import com.atm.model.entities.ConfirmationToken;
import com.atm.model.entities.Role;
import com.atm.model.entities.User;
import com.atm.core.utils.validators.UserNameExistsValidator;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/* ISP > extending the UserDetailsService interface
 has broken the ISP not all concrete classes might
 implement that interface, so we do not extend it
 instead we implement it in this class
 it is already an interface, so no need to add our
 own one.
 */
/*
    In addition, now i broke the UserService interface
    to add another one with only save() method, we have
    another class that won't implement all methods in our
    service interface. just the one.
 */
@AllArgsConstructor
@Service
public class UserManager implements UserService, UserDetailsService {
    private UserDao userDao;
    private DtoEntityConverter converter;
    private PasswordEncoderBean passwordEncoder;
    private ConfirmationTokenDao confirmationTokenDao;
    private MessageServices messageServices;
    private ConfirmationTokenServices confirmationTokenServices;
    private RoleDao roleDao;
    private EmailSender emailSender;


    @Override
    public String save(UserDto userDto) throws EmailExistsException {
        if (new UserNameExistsValidator(userDao).validate(userDto.getEmail()))
            throw new EmailExistsException(messageServices.getMessage("err.email.exists"));
        User user = (User) converter.dtoToEntity(userDto, new User());
        // Encrypting password
        user.setPassword(passwordEncoder.passwordEncoder().encode(user.getPassword()));
        user.setEnabled(false);
        user.setAccountNonLocked(1);
        // if no users, first role is Admin
        if (userDao.count() == 0)
            user.setRoles(List.of(getOrCreateRole("ROLE_ADMIN")));
        else
            user.setRoles(List.of(getOrCreateRole("ROLE_USER")));
        user.setSlug(new SlugGenerator().slug(userDto.getEmail()));
        userDao.save(user);

//        // TODO: create confirmation token
        ConfirmationToken confirmationToken = newConfirmationToken(user);
        confirmationTokenServices.saveConfirmationToken(confirmationToken);

        // TODO: Send confirmation email
        String link = "http://localhost:8080/atm/user/verify?token=" + confirmationToken.getToken();
        emailSender.send(user.getEmail(),
                buildEmail(user.getFirstName() + " " + user.getLastName(),
                        link));
        return confirmationToken.getToken();
    }

    private String buildEmail(String name, String link) {
        return  "<p> Hi, "+ name + ", </p>"+
                "<p>Thank you for registering with us,"+"" +
                "Please, follow the link below to complete your registration.</p>"+
                "<a href=\"" + link + "\">Verify your email to activate your account</a>"+
                "<p> Thank you <br> Users Registration Portal Service";
    }

    private Role getOrCreateRole(String name) {
        Optional<Role> role = roleDao.findByName(name);
        return role.orElseGet(() -> roleDao.save(new Role(name)));
    }

    private ConfirmationToken newConfirmationToken(User user) {
        String token = UUID.randomUUID().toString();
        return ConfirmationToken.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                // Minutes has to come from configuration file
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
    }

    @Override
    public UserDto findByEmail(String email) {
        return (UserDto) converter.entityToDto(
                userDao.findByEmail(email), UserDto.class
        );
    }


    @Override
    public String  confirmToken(String token) throws AccountInactiveException {
        // TODO: Confirm token
        ConfirmationToken confirmationToken = confirmationTokenDao.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("No token found!"));

        if (confirmationToken.getConfirmedAt() != null)
            throw new AccountInactiveException("Email is already confirmed!");

        LocalDateTime expiresAt = confirmationToken.getExpiredAt();
        if (expiresAt.isBefore(LocalDateTime.now())) {
            // TODO: resend confirmation token (create new one)
            confirmationTokenDao.save(newConfirmationToken(
                    confirmationToken.getUser()
            ));
            // TODO: Resend confirmation email
            throw new AccountInactiveException("Token is already expired, check your account" +
                    " for new token just been sent..");
        }
        // Update confirmation token, confirmedAt
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenDao.save(confirmationToken);
        // TODO: Enable user's account
        User user = confirmationToken.getUser();
        user.setEnabled(true);
        userDao.save(user);
        return "confirmed";
    }

    @Override
    public String update(UserDetailsDto userDto, String slug) throws Exception {
        User user = userDao.findByEmail(userDto.getEmail());
        if (!passwordEncoder.passwordEncoder().matches(userDto.getPassword(), user.getPassword()))
            throw new PasswordMisMatchException(messageServices.getMessage("err.psd.mismatch"));
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        return userDao.save(user)+" your details are updated";
    }

    @Override
    public String delete(Long id) {
        return "User deleted";
    }

    @Override
    public List<UserDto> users() {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException(messageServices.getMessage("err.username.notfound"));
        }
        return new CustomUserDetailsDto(user);

    }

    // Mapping Roles GrantedAuthority // SRP Violation
    public List<? extends GrantedAuthority> mapRolesToAuthorities(List<Role> roles){
        return roles.stream().map(
                // Changing Roles to SimpleGrantedAuthority Object
                role ->  new SimpleGrantedAuthority(role.getName())
        ).collect(Collectors.toList());
    }
}
