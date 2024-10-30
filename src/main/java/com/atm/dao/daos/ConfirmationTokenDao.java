package com.atm.dao.daos;

import com.atm.model.entities.ConfirmationToken;

public interface ConfirmationTokenDao {
    ConfirmationToken findByToken(String token);
    boolean isExpired(String token);
    ConfirmationToken save(ConfirmationToken token);
}
