package com.atm.dao.concretes;

import com.atm.dao.daos.TempUserDao;
import com.atm.model.dtos.TempUser;
import io.lettuce.core.RedisException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

@Repository
public class TempUserDaoImpl implements TempUserDao {

    private RedisTemplate redisTemplate;

    public TempUserDaoImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private final String KEY = "temp-user";
    @Override
    public void save(TempUser tempUser) {
        redisTemplate.opsForHash().put(KEY, tempUser.getEmail(), tempUser);
    }

    @Override
    public Optional<TempUser> findByEmail(String email) {
        return Optional.of((TempUser) Objects.requireNonNull(redisTemplate
                .opsForHash().get(KEY, email)));
    }

    @Override
    public void updateNotConfirmed(String email) {
        String key = KEY; // Use the existing "temp-user" hash key
        TempUser tempUser = (TempUser) redisTemplate.opsForHash().get(key, email);
        if (tempUser != null) {
            tempUser.setNotConfirmed(false); // Update the field
            redisTemplate.opsForHash().put(key, email, tempUser); // Save updated object
            System.out.println("Updated notConfirmed value: " +
                    findByEmail(email).get().isNotConfirmed());
        } else {
            System.out.println("No TempUser found for email: " + email);
        }

    }


}
