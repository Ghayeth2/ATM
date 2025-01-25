package com.atm.business.concretes;

import com.atm.business.abstracts.*;
import com.atm.core.bean.PasswordEncoderBean;
import com.atm.core.exceptions.PasswordMisMatchException;
import com.atm.core.utils.converter.DtoEntityConverter;
import com.atm.core.utils.strings_generators.StringGenerator;
import com.atm.dao.daos.UserDao;
import com.atm.model.dtos.CustomUserDetailsDto;
import com.atm.model.dtos.TempUser;
import com.atm.model.dtos.UserDetailsDto;
import com.atm.model.dtos.UserDto;
import com.atm.model.entities.ConfirmationToken;
import com.atm.model.entities.User;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
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
    private TempUserServices tempUserServices;
    private BCryptPasswordEncoder passwordEncoder;
    private EmailSenderServices emailSenderServices;

    @SneakyThrows
    @Override
    public void save(String token) {
        // Extracting user details from Temp Memory
        User user = getUser(token);
        // Assign role to user
        assignRole(user);
        // Save the new user
        System.out.println("user to be saved:"+
                user.getRoles()+" "+user.getSlug()
        +" "+user.getFirstName());
        userDao.save(user);
        System.out.println("After user is saved if it is reaching here..");
    }

    // User private Helper orchestrated methods

    private void assignRole(User user) {
        if (userDao.count() == 0)
            user.setRoles(List.of(roleServices
                    .getOrCreateRole("ROLE_ADMIN")));
        else
            user.setRoles(List.of(roleServices.getOrCreateRole("ROLE_USER")));
    }

    private User getUser(String token) {
        //
        TempUser temp = tempUserServices.findByToken(token);
        tempUserServices.updateNotConfirmed(temp.getEmail());
        User user = User.builder().firstName(temp.getFirstName())
                .lastName(temp.getLastName()).email(temp.getEmail())
                .password(passwordEncoder.encode(temp.getPassword()))
                .build();
        user.setAccountNonLocked(1);
        user.setSlug(new StringGenerator().slug(user.getEmail()));
        return user;
    }

    // User private Helper orchestrated methods

    @Override
    public UserDto findByEmail(String email) {
        return (UserDto) converter.entityToDto(
                userDao.findByEmail(email), UserDto.class
        );
    }

    private String createAndSaveToken(String email) {
        ConfirmationToken confirmationToken = confirmationTokenServices
                .newConfirmationToken(email);
        confirmationTokenServices.saveConfirmationToken(confirmationToken);
        return confirmationToken.getToken();
    }

    @Override
    public void resetPasswordSender(String email) {
        // Create and save confirmation token
        String token = createAndSaveToken(email);
        String link = "http://localhost:8080/atm/user/reset?token=" + token;
        String message = "<p>Please, follow the link to reset your password." + "" +
                "</p>" +
                "<a href=\"" + link + "\">Reset Password</a>";
        emailSenderServices.send(email, message);
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
