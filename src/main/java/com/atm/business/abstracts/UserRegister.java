package com.atm.business.abstracts;

import com.atm.core.exceptions.EmailExistsException;
import com.atm.model.dtos.UserDto;
/*
 SOLID
    SRP
    OCP
    LSP
    ISP
    DIP
 */
public interface UserRegister {
    void save(UserDto userDto) throws EmailExistsException;
}
