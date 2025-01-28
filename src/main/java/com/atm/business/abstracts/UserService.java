package com.atm.business.abstracts;

import com.atm.model.dtos.UserDetailsDto;
import com.atm.model.dtos.UserDto;
import com.atm.model.entities.User;

import java.util.List;

// UserDetailsServices might have broken ISP & SRP
public interface UserService {

    void save(String token);
    String update(UserDetailsDto dto, String slug);
    String delete(String slug);
    // For backend usage only
    User findBySlug (String slug);
    List<UserDto> findAll();
    UserDto findByEmail(String email);
    // User cannot delete himself, Admin can
    void handleResetPasswortMailSending(String email);
    String resetPassword(String password, String slug);
    User findUserByToken(String token);

}
