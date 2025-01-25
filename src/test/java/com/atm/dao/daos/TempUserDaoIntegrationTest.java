package com.atm.dao.daos;

import com.atm.dao.concretes.TempUserDaoImpl;
import com.atm.model.dtos.TempUser;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import org.testcontainers.containers.GenericContainer;


@Testcontainers
@DataRedisTest @Log4j2
@Import(TempUserDaoImpl.class)
public class TempUserDaoIntegrationTest {

    @Container
    static GenericContainer<?> redisContainer =
            new GenericContainer<>("redis:7.4.1-alpine3.20")
            .withExposedPorts(6379);

    @Autowired
    private TempUserDaoImpl tempUserDao;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
    }

    @Test
    void tempUserDao_SaveTempUser() {
        // Prepare data will be saved to Redis server
        TempUser tempUser = TempUser.builder().firstName("ghayeth")
                .lastName("masri").email("ghayeth.msri@gmail.com")
                .password("password").build();
        // Saving data to redis server from TempUserDao class
        tempUserDao.save(tempUser);
    }

    @Test
    void tempUserDao_FindByEmail() {
        Optional<TempUser> res = tempUserDao
                .findByEmail("ghayeth.msri@gmail.com");
        // Asserting the result
        Assertions.assertEquals(res.get().getFirstName(), "ghayeth");

    }
}
