package com.atm.business.concretes;

import com.atm.business.abstracts.RoleServices;
import com.atm.model.entities.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration testing for RoleServices to ensure Roles are
 * being managed correctly.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class RoleManagerIntegrationTest {

    @Autowired
    private RoleServices roleServices;

    @Container
    private static GenericContainer<?> mysql =
            new GenericContainer<>("mysql:8.0") // Specify a stable version
                    .withExposedPorts(3306)
                    .withEnv("MYSQL_DATABASE", "atm")
                    .withEnv("MYSQL_USER", "root")
                    .withEnv("MYSQL_ROOT_PASSWORD", "password123") // Set a root password
                    .waitingFor(Wait.forListeningPort())
                    .waitingFor(Wait.forLogMessage(".*ready for connections.*\\n", 1))
                    .withStartupTimeout(Duration.ofMinutes(5)); // Increase timeout if needed

    @BeforeAll
    static void startUP() {
        mysql.start();
    }

    @AfterAll
    static void shutDown() {
        mysql.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        System.out.println("Host: " + mysql.getHost());
        System.out.println("Port: " + mysql.getMappedPort(3306));
        registry.add("spring.datasource.url", () ->
                String.format("jdbc:mysql://%s:%d/atm",
                        mysql.getHost(),
                        mysql.getMappedPort(3306)));
        registry.add("spring.datasource.username", () -> "root");
        registry.add("spring.datasource.password", () -> "password123"); // Match the root password
        registry.add("spring.jpa.properties.hibernate.dialect",
                () -> "org.hibernate.dialect.MySQL8Dialect");
        registry.add("spring.jpa.properties.hibernate.show_sql",
                () -> true);
    }

    /**
     * Integrating Testing getOrCreateRole method,
     *  creating ROLE_ADMIN
     */
    @Test
    void roleServices_GetOrCreateRoleMethod_WillCreateRole () {
        // Logger to identify current phase
        System.out.println("Creating new ROLE_ADMIN phase's role.isEmpty():");
        // Creating Role record
        Role role = roleServices.getOrCreateRole("ROLE_ADMIN");
        // Asserting the role is successfully created
        assertNotNull(role);
    }

    /**
     * Integrating Testing getOrCreateRole method,
     *  getting the inserted role (ROLE_ADMIN)
     */
    @Test
    void roleServices_GetOrCreateRoleMethod_WillReturnRole () {
        // Logger to identify current phase
        System.out.println("Getting inserted ROLE_ADMIN phase's role.isEmpty():");
        // Invoking getOrCreateRole() method to get ROLE_ADMIN
        Role role = roleServices.getOrCreateRole("ROLE_ADMIN");
        // Asserting role's value is ROLE_ADMIN
        Assertions.assertThat(role.getName()).
        isEqualTo("ROLE_ADMIN");
    }

}