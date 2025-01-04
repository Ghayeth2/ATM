package com.atm.units.repository;


import com.atm.EmbeddedRedisConnection;
import com.atm.dao.concretes.ConfirmationTokenDaoImpl;
import com.atm.dao.daos.ConfirmationTokenDao;
import com.atm.model.entities.ConfirmationToken;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;

import org.springframework.context.annotation.Import;
import redis.embedded.RedisServer;

import java.time.LocalDateTime;
import java.util.UUID;

// it is unit testing, so i can test this one with H2DATABASE too

/**
 * Test class for Repo of confirmation token working with Redis Server
 */
@DataRedisTest
@Import(ConfirmationTokenDaoImpl.class)
public class ConfirmationTokenRepositoryTest extends EmbeddedRedisConnection {
    /*
    U need to include embedded-redis in pom.xml
    and since u r working with redis, u need to
    manually start and stop the redis server
    use @BeforeAll and @AfterAll
    u will use RedisServer object

    And u can inject ur ConfirmationTokenDao bean
    to complete the test.
     */
//    private static RedisServer redisServer;
    private static String token;
    private static ConfirmationToken cToken;

    @Autowired
    private ConfirmationTokenDao tokenDao;

    @BeforeEach // equivalent for @BeforeClass
     void beforeTests() throws Exception {
        // Create unique token
        token = UUID.randomUUID().toString();
        // Fill confirmation token object
        cToken = ConfirmationToken.builder()
                .token(token).email("test-redis@atm.com")
                .expiresAt(LocalDateTime.now().plusMinutes(1))
                .build();
    }

    @Test
    void shouldSaveToken_WhenValidTokenIsProvided() {
        ConfirmationToken savedToken = tokenDao.save(cToken);
        Assertions.assertThat(savedToken.getEmail())
                .isEqualTo(cToken.getEmail());
    }

    @Test
    void shouldUpdateConfirmedAt_WhenEmailIsConfirmed(){
        // Save to Redis server
        tokenDao.save(cToken);
        // Updating confirmedAt
        ConfirmationToken aToken = tokenDao.findByToken(cToken.getToken());
        aToken.setConfirmedAt(LocalDateTime.now());
        ConfirmationToken updatedToken = tokenDao.save(aToken);
        Assertions.assertThat(updatedToken.getConfirmedAt()).isNotNull();
    }

    @Test
    void shouldReturnExpired_WhenExpired() {
        // Saving the token
        tokenDao.save(cToken);
        // Getting saved token
        ConfirmationToken aToken = tokenDao.findByToken(cToken.getToken());
        // Update to simulate expiration
        aToken.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        ConfirmationToken updatedToken = tokenDao.save(aToken);
        // Asserting everything worked as expected
        Assertions.assertThat(updatedToken.getExpiresAt()).isBefore(LocalDateTime.now());
    }

    @Test
    void shouldReturnConfirmationToken_WhenTokenIsGiven() {
        tokenDao.save(cToken);
        ConfirmationToken foundCToken = tokenDao.findByToken(token);
        Assertions.assertThat(foundCToken.getEmail()).isEqualTo(cToken.getEmail());
    }
}
