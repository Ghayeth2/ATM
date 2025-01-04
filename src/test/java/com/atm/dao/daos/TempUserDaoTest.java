package com.atm.dao.daos;

import com.atm.EmbeddedRedisConnection;
import com.atm.dao.concretes.TempUserDaoImpl;
import com.atm.model.dtos.TempUser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Testing TempUserDaoImp class for Redis server.
 */
@DataRedisTest
@Import(TempUserDaoImpl.class)
class TempUserDaoTest extends EmbeddedRedisConnection {

    @Autowired
    private TempUserDao tempUserDao;

    private TempUser tempUser;
    @BeforeEach
    void setUp() {
        tempUser = TempUser.builder().firstName("fir").lastName("last")
                .email("test@gmail.com").build();
    }

    /**
     * Save TempUser test method.
     */
    @Test
    void tempUserDao_SaveTempUser() {
        // Saving the tempUser
        tempUserDao.save(tempUser);
        // Calling findByEmail to check if user is saved or not
        TempUser res = tempUserDao.findByEmail(tempUser.getEmail()).get();
        // Asserting result is valid
        Assertions.assertThat(res.getFirstName())
                .isEqualTo(tempUser.getFirstName());
    }

    /**
     * Finding TempUser by email test.
     */
    @Test
    void tempUserDao_FindTempUserByEmail() {
        // Saving a temp user
        tempUserDao.save(tempUser);
        // Retrieving saved temp user
        TempUser res = tempUserDao.findByEmail(tempUser.getEmail()).get();
        // Asserting equality
        Assertions.assertThat(res.getEmail())
                .isEqualTo(tempUser.getEmail());
    }
}