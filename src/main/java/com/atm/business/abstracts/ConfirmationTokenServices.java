package com.atm.business.abstracts;

import com.atm.core.exceptions.AccountInactiveException;
import com.atm.model.dtos.TempUser;
import com.atm.model.entities.ConfirmationToken;
import com.atm.model.entities.User;

public interface ConfirmationTokenServices {
    void saveConfirmationToken(ConfirmationToken confirmationToken);
    String  confirmToken(String token) throws AccountInactiveException;
    ConfirmationToken newConfirmationToken(String email);
    ConfirmationToken findByToken(String token);
    boolean isTokenValid(String token);
}
