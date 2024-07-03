package com.atm.business.abstracts;

import com.atm.core.exception.EmailExistsException;
import com.atm.model.dtos.UserDto;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

// UserDetailsServices might have broken ISP & SRP
public interface UserService {
    String  update(UserDto userDto, Long id);
    String save(UserDto userDto) throws EmailExistsException;

    // User cannot delete himself, Admin can
    String  delete(Long id);
    List<UserDto> users();
}
