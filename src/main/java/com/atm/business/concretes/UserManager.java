package com.atm.business.concretes;

import com.atm.business.abstracts.ConfirmationTokenServices;
import com.atm.business.abstracts.EmailSenderServices;
import com.atm.business.abstracts.RoleServices;
import com.atm.business.abstracts.UserService;
import com.atm.core.bean.PasswordEncoderBean;
import com.atm.core.exceptions.EmailExistsException;
import com.atm.core.exceptions.PasswordMisMatchException;
import com.atm.core.utils.converter.DtoEntityConverter;
import com.atm.core.utils.stringsOPS.SlugGenerator;
import com.atm.dao.UserDao;
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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service @Log4j2
public class UserManager implements UserService, UserDetailsService {
    private UserDao userDao;
    private DtoEntityConverter converter;
    private PasswordEncoderBean passwordEncoder;
    private MessageServices messageServices;
    private ConfirmationTokenServices confirmationTokenServices;
    // TODO: SRP > Role DAO is violating it
    private RoleServices roleServices;
    private EmailSenderServices emailSenderServices;


    @SneakyThrows
    @Override
    public String save(UserDto userDto)  {
        if (new UserNameExistsValidator(userDao).validate(userDto.getEmail()))
            throw new EmailExistsException(messageServices.getMessage("err.email.exists"));
        User user = (User) converter.dtoToEntity(userDto, new User());
        // Encrypting password
        user.setPassword(passwordEncoder.passwordEncoder().encode(user.getPassword()));
        user.setEnabled(false);
        user.setAccountNonLocked(1);
        // if no users, first role is Admin
        if (userDao.count() == 0)
            user.setRoles(List.of(roleServices.getOrCreateRole("ROLE_ADMIN")));
        else
            user.setRoles(List.of(roleServices.getOrCreateRole("ROLE_USER")));
        user.setSlug(new SlugGenerator().slug(userDto.getEmail()));
        userDao.save(user);

//        // TODO: create confirmation token
        ConfirmationToken confirmationToken = confirmationTokenServices.newConfirmationToken(user);
        confirmationTokenServices.saveConfirmationToken(confirmationToken);

        // TODO: Send confirmation email
        String link = "http://localhost:8080/atm/user/verify?token=" + confirmationToken.getToken();
        emailSenderServices.send(user.getEmail(),
                buildConfirmEmailBody(user.getFirstName() + " " + user.getLastName(),
                        link));
        return confirmationToken.getToken();
    }

    private String buildConfirmEmailBody(String name, String link) {
        return  "<p> Hi, "+ name + ", </p>"+
                "<p>Thank you for registering with us,"+"" +
                "Please, follow the link below to complete your registration.</p>"+
                "<a href=\"" + link + "\">Verify your email to activate your account</a>"+
                "<p> Thank you <br> Users Registration Portal Service";
    }

    @Override
    public UserDto findByEmail(String email) {
        return (UserDto) converter.entityToDto(
                userDao.findByEmail(email), UserDto.class
        );
    }

    @Override
    public void resetPasswordSender(String email) {
        User user = userDao.findByEmail(email);
        ConfirmationToken cToken = confirmationTokenServices.newConfirmationToken(user);
        // it should be in temp memory Redis
        confirmationTokenServices.saveConfirmationToken(cToken);
//        log.info("Generated token reset : "+cToken.getCreatedAt()+ " " + cToken.getExpiredAt());
        String link = "http://localhost:8080/atm/user/reset?token=" + cToken.getToken();
        String message = "<p>Please, follow the link to reset your password."+"" +
                "</p>"+
                "<a href=\"" + link + "\">Reset Password</a>";
        emailSenderServices.send(user.getEmail(), message);
    }

    @Override
    public User findUserByToken(String token) {
        return confirmationTokenServices.findConfirmationToken(token)
                .getUser();
    }

    @SneakyThrows
    @Override
    public String resetPassword(String password, String slug) {
        User user = userDao.findBySlug(slug).get();
        user.setPassword(passwordEncoder.passwordEncoder().encode(password));
        userDao.save(user);
        return "Password reset successfully";
    }


    @Override
    public String update(UserDetailsDto userDto, String slug) {
        User user = userDao.findByEmail(userDto.getEmail());
        if (!passwordEncoder.passwordEncoder().matches(userDto.getPassword(), user.getPassword()))
            try {
                throw new PasswordMisMatchException(messageServices.getMessage("err.psd.mismatch"));
            } catch (PasswordMisMatchException e) {
                throw new RuntimeException(e);
            }
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        return userDao.save(user)+" your details are updated";
    }

    @Override
    // ResponseEntity<>
    public String delete(String slug) {
        Optional<User> user = userDao.findBySlug(slug);
        user.ifPresent(value -> userDao.delete(value));
        return "User deleted";
    }


    @Override
    public UserDto findBySlug(String slug) {
        Optional<User> user = userDao.findBySlug(slug);
        if (user.isEmpty())
            throw new UsernameNotFoundException(messageServices.
                    getMessage("err.user.not.found"));
        return (UserDto) converter
                .entityToDto(userDao.findBySlug(slug).get(), UserDto.class);
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

    // Mapping Roles GrantedAuthority // SRP Violation
    public List<? extends GrantedAuthority> mapRolesToAuthorities(List<Role> roles){
        return roles.stream().map(
                // Changing Roles to SimpleGrantedAuthority Object
                role ->  new SimpleGrantedAuthority(role.getName())
        ).collect(Collectors.toList());
    }
}
