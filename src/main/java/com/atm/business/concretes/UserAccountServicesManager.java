package com.atm.business.concretes;

import com.atm.business.abstracts.ConfirmationTokenServices;
import com.atm.business.abstracts.UserAccountServices;
import com.atm.dao.daos.UserDao;
import com.atm.model.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class UserAccountServicesManager implements UserAccountServices {
    private UserDao userDao;
    private ConfirmationTokenServices tokenService;
    public static final int MAX_FAILED_ATTEMPTS = 3;
    private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24 hours

    @Autowired
    public UserAccountServicesManager(UserDao userDao,
                                      ConfirmationTokenServices tokenService) {
        this.userDao = userDao;
        this.tokenService = tokenService;
    }

    @Override
    public void increaseFailedAttempts(User user) {
        int newFailedA = user.getFailedAttempts()+1;
        userDao.updateFailedAttempts(newFailedA, user.getEmail());
    }

    @Override
    public void resetFailedAttempts(String email) {
        userDao.updateFailedAttempts(0, email);
    }

    @Override
    public void lock(User user) {
        user.setAccountNonLocked(0);
        user.setFirstName(user.getFirstName());
        user.setLastName(user.getLastName());
        user.setEmail(user.getEmail());
        user.setPassword(user.getPassword());
        user.setLockTime(new Date());
        userDao.save(user);
    }

    @Override
    public boolean unlockWhenTimeExpired(User user) {
        long lockTimeInMillis = user.getLockTime().getTime();
        long currentTimeInMillis = System.currentTimeMillis();
        if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
//            log.info("is it entering here?");
            user.setAccountNonLocked(1);
            user.setLockTime(null);
            user.setFailedAttempts(0);
            userDao.save(user);
            return true;
        }
        return false;
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public void activateAccount(String token) {
        Optional<User> userOptional = Optional.of(userDao.findByEmail(
                tokenService.findByToken(token)
                        .getEmail()
        ));
        userOptional.get().setEnabled(true);
        userOptional.ifPresent(user -> userDao.save(user));
    }


}
