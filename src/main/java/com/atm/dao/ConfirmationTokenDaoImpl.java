package com.atm.dao;

import com.atm.model.entities.ConfirmationToken;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Log4j2
@Repository
public class ConfirmationTokenDaoImpl implements ConfirmationTokenDao {
    private final String HASH_KEY = "confirmation_token";
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${verification.code.expiration}")
    int verificationCodeExpiration;
    // Create or update existing one
    @Override
    public ConfirmationToken save(ConfirmationToken token) {
        String valueKey = HASH_KEY+":"+token.getToken();
        redisTemplate.opsForHash().put(HASH_KEY, token.getToken(), token);
        // didn't use the individual expiration since Redis is not persisting my data
        // So they will be gone in case of server restarting.
        // setting expiration for each token under the hash key (confirmation-token)
//        redisTemplate.opsForValue().set(valueKey, token,  verificationCodeExpiration, TimeUnit.MINUTES);
        return token;
    }
    @Override
    public boolean isExpired(String token) {
        log.info("token into Redis: "+HASH_KEY+":"+token);
        String key = HASH_KEY+":"+token;
        if (!redisTemplate.hasKey(key))
            return true;
        else
            return false;
    }
    @Override
    public ConfirmationToken findByToken(String token) {
        String redisKey = HASH_KEY+":"+token;
        return (ConfirmationToken) redisTemplate.opsForHash().get(HASH_KEY, token);
    }
}
