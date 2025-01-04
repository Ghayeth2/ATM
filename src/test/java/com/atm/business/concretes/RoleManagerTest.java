package com.atm.business.concretes;

import com.atm.business.abstracts.RoleServices;
import com.atm.model.entities.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;
@Testcontainers
@SpringBootTest
class RoleManagerTest {

    @Autowired
    private RoleServices roleServices;

    @Container
    static GenericContainer<?> container =
            new GenericContainer<>("mysql:8.0")
                    .withExposedPorts(3306)
                    .withEnv("MYSQL_DATABASE", "test")
                    .withEnv("MYSQL_ROOT_PASSWORD", "test")
                    .waitingFor(Wait.forListeningPort());

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        String host = container.getHost();
        int port = container.getMappedPort(3306);
        registry.add(
                "spring.datasource.url",
                () -> String.format("jdbc:mysql://%s:%d/%s", host, port, "test")
        );
        registry.add("spring.datasource.username", () -> "root");
        registry.add("spring.datasource.password", () -> "test");
    }

    @Test
    void roleServices_CreateNewRole() {
        roleServices.getOrCreateRole("ROLE_USER");
        Role role = roleServices.getOrCreateRole("ROLE_USER");
        assertNotNull(role);
    }
}
