package com.nhnacademy.tokenservice.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class RedisDaoTest {

    @Mock
    private RedisTemplate<String,String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOps;

    @InjectMocks
    private RedisDao redisDao;

    private String key = "test-key";
    private String value = "test-value";
    private long expiration =3600L;

    @Test
    @DisplayName("key-value 저장")
    void save() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        redisDao.save(key, value, expiration);

        verify(valueOps, Mockito.times(1)).set(key, value, expiration, TimeUnit.MILLISECONDS);
    }

    @Test
    @DisplayName("key-value 조회")
    void get() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(key)).thenReturn(value);

        String actual = redisDao.get(key);

        verify(valueOps, Mockito.times(1)).get(key);

        assertEquals(value, actual);
    }

    @Test
    @DisplayName("key-value 조회 - null")
    void get_key_null() {
        String actual = redisDao.get(null);

        assertNull(actual);
    }

    @Test
    @DisplayName("key 삭제")
    void delete() {

        redisDao.delete(key);

        verify(redisTemplate, Mockito.times(1)).delete(key);
    }

    @Test
    @DisplayName("key 존재 여부 - true")
    void has() {
        when(redisTemplate.hasKey(key)).thenReturn(true);

        boolean actual = redisDao.has(key);

        assertTrue(actual);
    }

    @Test
    @DisplayName("key 존재 여부 - false")
    void has_false() {
        when(redisTemplate.hasKey(key)).thenReturn(false);

        boolean actual = redisDao.has(key);

        assertFalse(actual);
    }

    @Test
    @DisplayName("key 존재 여부 - key is null")
    void has_key_null() {
        boolean actual = redisDao.has(null);

        assertFalse(actual);
    }
}