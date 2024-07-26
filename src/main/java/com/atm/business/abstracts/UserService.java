package com.atm.business.abstracts;

import com.atm.core.exceptions.EmailExistsException;
import com.atm.model.dtos.UserDetailsDto;
import com.atm.model.dtos.UserDto;

import java.util.List;

// UserDetailsServices might have broken ISP & SRP
public interface UserService {
    String  update(UserDetailsDto userDto, String slug) throws Exception;
    String save(UserDto userDto) throws EmailExistsException;
    UserDto findByEmail(String email);
    // User cannot delete himself, Admin can
    String  delete(Long id);
    List<UserDto> users();
    String confirmToken(String token);
}
