package com.atm.business.abstracts;

import com.atm.model.entities.ConfirmationToken;

public interface ConfirmationTokenServices {
    void saveConfirmationToken(ConfirmationToken confirmationToken);
}
