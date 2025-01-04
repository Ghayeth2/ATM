package com.atm.business.abstracts;

import com.atm.model.dtos.TempUser;
import com.atm.model.dtos.UserDto;

import java.util.Optional;

public interface TempUserServices {
    String save(UserDto tempUser);
    Optional<TempUser> findByUsername(String username);
    TempUser findByToken(String token);
    void updateNotConfirmed(String email);
}
