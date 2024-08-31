package com.atm.business.concretes;

import com.atm.business.abstracts.RoleServices;
import com.atm.dao.RoleDao;
import com.atm.model.entities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleManager implements RoleServices {
    @Autowired
    private RoleDao roleDao;

    @Override
    public Role getOrCreateRole(String roleName) {
        Optional<Role> role = roleDao.findByName(roleName);
        return role.orElseGet(() -> roleDao.save(new Role(roleName)));
    }
}
