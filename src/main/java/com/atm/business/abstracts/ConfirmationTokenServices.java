package com.atm.business.abstracts;

import com.atm.core.exceptions.AccountInactiveException;
import com.atm.model.entities.ConfirmationToken;
import com.atm.model.entities.User;

public interface ConfirmationTokenServices {
    void saveConfirmationToken(ConfirmationToken confirmationToken);
    boolean isTokenValid(String confirmationToken);
    String  confirmToken(String token) throws AccountInactiveException;
    ConfirmationToken newConfirmationToken(User user);
    ConfirmationToken findConfirmationToken(String confirmationToken);
}
