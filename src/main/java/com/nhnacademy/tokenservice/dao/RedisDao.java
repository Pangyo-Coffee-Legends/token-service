package com.nhnacademy.tokenservice.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisDao {
    private final RedisTemplate<String, String> redisTemplate;


    public void save(String key, String value, long expiration) {
        redisTemplate.opsForValue().set(key, value, expiration, TimeUnit.MILLISECONDS);
    }

    public String get(String key){
        if(key == null){
            return null;
        }

        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean has(String key) {
        if(key == null){
            return false;
        }

        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
