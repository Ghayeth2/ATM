package com.atm.units.service;
import com.atm.business.concretes.RoleManager;
import com.atm.dao.daos.RoleDao;
import com.atm.model.entities.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class RoleManagerTest {

    @Mock
    private RoleDao roleDao;

    @InjectMocks
    private RoleManager roleManager;

    // Get or create a Role
    @Test
    void shouldGetOrCreateARole_WhenItIsInvoked() {
        Role role = Role.builder().name("ROLE_ADMIN").build();
        when(roleDao.findByName(anyString())).thenReturn(
                Optional.of(role)
        );
        Role res = roleManager.getOrCreateRole("ROLE_ADMIN");
        Assertions.assertThat(res).isEqualTo(role);
    }
}
