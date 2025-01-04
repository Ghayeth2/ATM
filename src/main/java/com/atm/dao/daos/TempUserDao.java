package com.atm.dao.daos;

import com.atm.model.dtos.TempUser;

import java.util.Optional;


public interface TempUserDao {
    void save(TempUser tempUser);
    Optional<TempUser> findByEmail(String email);
    void updateNotConfirmed(String email);
}
