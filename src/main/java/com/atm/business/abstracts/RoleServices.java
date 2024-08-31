package com.atm.business.abstracts;

import com.atm.model.entities.Role;

public interface RoleServices {
    Role getOrCreateRole (String roleName);
}
