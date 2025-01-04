package com.atm.units.repository;

import com.atm.core.config.TestAuditingConfig;
import com.atm.core.utils.strings_generators.SlugGenerator;
import com.atm.dao.daos.UserDao;
import com.atm.model.entities.User;
import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
@Log4j2
@Import(TestAuditingConfig.class)
public class UserRepositoryTest {
    @Autowired
    private UserDao userDao;
    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldSaveUser_WhenUserIsProvided() {
        // Arrange
        User user = User.builder()
                .password("password").firstName("first").lastName("last")
                .accountNonLocked(1).failedAttempts(0)
                .email("user@user.com")
                .build();
        user.setSlug(new SlugGenerator().slug("user@user.com"));
        // Act
        User savedUser = userDao.save(user);
        // Assert
        Assertions.assertNotEquals(0, savedUser.getId());
        Assertions.assertNotNull(savedUser);
    }

    @Test
    void shouldFindUser_WhenEmailIsProvided() {
        User user = User.builder()
                .email("test@atm.com").firstName("first").lastName("last")
                .accountNonLocked(1).failedAttempts(0)
                .password("password")
                .build();
        user.setSlug(new SlugGenerator().slug("test@atm.com"));
        userDao.save(user);
        User foundUser = userDao.findByEmail("test@atm.com");
        Assertions.assertNotNull(foundUser);
    }

    @Test
    void shouldDeleteUser_WhenUserIsProvided() {
        // Arrange
        User user = User.builder()
                .email("test@atm.com").firstName("first").lastName("last")
                .accountNonLocked(1).failedAttempts(0)
                .password("password")
                .build();
        user.setSlug(new SlugGenerator().slug("test@atm.com"));
        // Act
        userDao.save(user);
        userDao.delete(user);
        boolean exists = userDao.existsByEmail("test@atm.com");
        // Assert
        Assertions.assertFalse(exists);
    }

    @Test
    void shouldReturnUsers_WhenFindAllIsCalled() {
        // Arrange
        User user = User.builder()
                .email("test@atm.com").firstName("first").lastName("last")
                .accountNonLocked(1).failedAttempts(0)
                .password("password")
                .build();
        user.setSlug(new SlugGenerator().slug("test@atm.com"));
        User user1 = User.builder()
                .email("atm@atm.com").firstName("first").lastName("last")
                .accountNonLocked(1).failedAttempts(0)
                .password("password")
                .build();
        user1.setSlug(new SlugGenerator().slug("atm@atm.com"));
        Iterable<User> users = Arrays.asList(user, user1);
        // Act
        userDao.saveAll(users);
        List<User> foundUsers = userDao.findAll();
        // Assert
        Assertions.assertNotNull(foundUsers);
        Assertions.assertEquals(2, foundUsers.size());
    }

    @Test
    void shouldReturnIfExists_WhenEmailIsProvided() {
        // Arrange
        User user = User.builder()
                .email("test@atm.com").firstName("first").lastName("last")
                .accountNonLocked(1).failedAttempts(0)
                .password("password")
                .build();
        user.setSlug(new SlugGenerator().slug("test@atm.com"));
        // Act
        String email = userDao.save(user).getEmail();
        boolean exists = userDao.existsByEmail(email);
        // Assert
        Assertions.assertTrue(exists);
    }

    @Test
    void shouldUpdateFailedAttempts_WhenFailedAttemptsIsProvided() {
        // Arrange
        User user = User.builder()
                .email("test@atm.com").firstName("first").lastName("last")
                .accountNonLocked(1).failedAttempts(0)
                .password("password")
                .build();
        user.setSlug(new SlugGenerator().slug("test@atm.com"));
        // Act
        User savedUser = userDao.save(user);
        int isUpdated = userDao.updateFailedAttempts(1, savedUser.getEmail());

        // Cleaning old user data out from cache, to retrieve my updated user details
        entityManager.clear();
        User updatedUser = userDao.findByEmail(savedUser.getEmail());

        // Assert
        Assertions.assertNotEquals(0, isUpdated);
        Assertions.assertNotEquals(0, updatedUser.getFailedAttempts());
    }
}
