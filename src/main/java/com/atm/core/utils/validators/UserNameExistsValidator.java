package com.atm.core.utils.validators;

import com.atm.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserNameExistsValidator {
    private final UserDao userDao;

    @Autowired
    public UserNameExistsValidator(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean validate(String username) {
        return userDao.existsByEmail(username);
    }

}
