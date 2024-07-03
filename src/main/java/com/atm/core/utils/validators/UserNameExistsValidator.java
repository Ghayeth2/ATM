package com.atm.core.utils.validators;

import com.atm.business.abstracts.UserRegister;
import com.atm.business.abstracts.UserService;
import com.atm.business.concretes.UserManager;
import com.atm.core.exception.EmailExistsException;
import com.atm.dao.UserDao;
import com.atm.model.dtos.UserDto;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
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
