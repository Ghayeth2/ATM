package com.atm.business.concretes;

import com.atm.business.abstracts.UserService;
import com.atm.core.bean.PasswordEncoderBean;
import com.atm.core.exception.EmailExistsException;
import com.atm.core.utils.converter.DtoEntityConverter;
import com.atm.core.utils.stringsOPS.SlugGenerator;
import com.atm.dao.UserDao;
import com.atm.model.dtos.CustomUserDetailsDto;
import com.atm.model.dtos.UserDto;
import com.atm.model.entities.Role;
import com.atm.model.entities.User;
import com.atm.core.utils.validators.UserNameExistsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
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
@Service
public class UserManager implements UserService, UserDetailsService {
    private UserDao userDao;
    private DtoEntityConverter converter;
    private PasswordEncoderBean passwordEncoder;

    @Autowired
    public UserManager(UserDao userDao, DtoEntityConverter converter, PasswordEncoderBean passwordEncoder) {
        this.userDao = userDao;
        this.converter = converter;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String save(UserDto userDto) throws EmailExistsException {
        if (new UserNameExistsValidator(userDao).validate(userDto.getEmail()))
            throw new EmailExistsException("Username is already existed, try login in");
        User user = (User) converter.dtoToEntity(userDto, new User());
        // Encrypting password
        user.setPassword(passwordEncoder.passwordEncoder().encode(user.getPassword()));
        user.setAccountNonLocked(1);
        // if no users, first role is Admin
        if (userDao.count() == 0)
            user.setRoles(List.of(new Role("ROLE_ADMIN")));
        else
            user.setRoles(List.of(new Role("ROLE_USER")));
        user.setSlug(new SlugGenerator().slug(userDto.getEmail()));
        userDao.save(user);
        return "User saved";
    }

    @Override
    public String update(UserDto userDto, Long id) {
        return "User updated";
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
            throw new UsernameNotFoundException("Invalid username or password!");
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
