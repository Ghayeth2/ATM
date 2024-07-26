package com.atm.business.concretes;

import com.atm.business.abstracts.ConfirmationTokenServices;
import com.atm.business.abstracts.UserService;
import com.atm.dao.ConfirmationTokenDao;
import com.atm.dao.UserDao;
import com.atm.model.entities.ConfirmationToken;
import com.atm.model.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service @AllArgsConstructor
public class ConfirmationTokenManager implements ConfirmationTokenServices {

    private final ConfirmationTokenDao confirmationTokenDao;

    @Override
    public void saveConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationTokenDao.save(confirmationToken);
    }


}
