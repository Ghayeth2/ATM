package com.atm.dao.concretes;

import com.atm.dao.daos.ConfirmationTokenDao;
import com.atm.dao.daos.TempUserDao;
import com.atm.model.entities.ConfirmationToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;
@Testcontainers
@Import(ConfirmationTokenDaoImpl.class)
@DataRedisTest
class ConfirmationTokenDaoImplTest {
    @Container
    static GenericContainer<?> redisContainer =
            new GenericContainer<>("redis:7.4.1-alpine3.20")
                    .withExposedPorts(6379);

    @Autowired
    private ConfirmationTokenDaoImpl confirmationTokenDao;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
    }

    @Test
    void confirmationTokenDao_SaveMethodWithRedisServer (){
        ConfirmationToken token = ConfirmationToken
                .builder().email("test@gmail.com")
                .token("tken").build();
        confirmationTokenDao.save(token);
        // Logs of running test container
        redisContainer.followOutput(frame -> System.out.println(frame.getUtf8String()));
    }

    @Test
    void confirmationTokenDao_FindingSavedTokenInRedisServerMethod () {
        ConfirmationToken token = confirmationTokenDao
                .findByToken("tken");
        // Logs of running test container
        redisContainer.followOutput(frame -> System.out.println(frame.getUtf8String()));
        Assertions.assertEquals("test@gmail.com", token.getEmail());
    }
}