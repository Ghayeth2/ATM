package com.atm.units.repository;

import com.atm.core.config.TestAuditingConfig;
import com.atm.dao.daos.RoleDao;
import com.atm.model.entities.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
@Import(TestAuditingConfig.class)
public class RoleRepositoryTest {
    @Autowired
    private RoleDao roleDao;

    @Test
    void itShouldFindRoleByName() {
        Role role = Role.builder().name("ROLE_ADMIN").build();
        roleDao.save(role);
        Role foundRole = roleDao.findByName("ROLE_ADMIN").get();
        Assertions.assertEquals("ROLE_ADMIN", foundRole.getName());
    }
}
