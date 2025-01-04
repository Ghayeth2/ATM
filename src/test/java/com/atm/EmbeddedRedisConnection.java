package com.atm;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import redis.embedded.RedisServer;

/**
 * Abstract super class for setting up Redis server port and starting it up
 * before the tests are executed and shutting it down after for each test class.
 */
public abstract class EmbeddedRedisConnection {

    protected static RedisServer redisServer;

    @BeforeAll
    static void startUpRedisServer() {
        redisServer = RedisServer.builder().port(6379).build();
        redisServer.start();
    }

    @AfterAll
    static void shutDownRedisServer() {
        redisServer.stop();
    }
}
