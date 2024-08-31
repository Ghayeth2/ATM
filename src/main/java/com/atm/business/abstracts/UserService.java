package com.atm.business.abstracts;

import com.atm.core.exceptions.AccountInactiveException;
import com.atm.core.exceptions.EmailExistsException;
import com.atm.model.dtos.UserDetailsDto;
import com.atm.model.dtos.UserDto;
import com.atm.model.entities.User;

import java.util.List;

// UserDetailsServices might have broken ISP & SRP
public interface UserService extends CRUDServices <UserDto, UserDetailsDto>{


    UserDto findByEmail(String email);
    // User cannot delete himself, Admin can
    void resetPasswordSender(String email);
    String resetPassword(String password, String slug);
    User findUserByToken(String token);

}
