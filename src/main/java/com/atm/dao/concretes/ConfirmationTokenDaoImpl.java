package com.atm.dao.concretes;

import com.atm.dao.daos.ConfirmationTokenDao;
import com.atm.model.entities.ConfirmationToken;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository
public class ConfirmationTokenDaoImpl implements ConfirmationTokenDao {
    private final String HASH_KEY = "confirmation_token";
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public ConfirmationToken save(ConfirmationToken token) {
        redisTemplate.opsForHash().put(HASH_KEY, token.getToken(), token);
        return token;
    }
    @Override
    public boolean isExpired(String token) {
        String key = HASH_KEY+":"+token;
        if (!redisTemplate.hasKey(key))
            return true;
        else
            return false;
    }
    @Override
    public ConfirmationToken findByToken(String token) {
        return (ConfirmationToken) redisTemplate.opsForHash().get(HASH_KEY, token);
    }
}
