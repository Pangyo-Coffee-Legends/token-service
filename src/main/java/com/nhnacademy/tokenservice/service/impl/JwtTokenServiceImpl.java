package com.nhnacademy.tokenservice.service.impl;

import com.nhnacademy.tokenservice.dao.RedisDao;
import com.nhnacademy.tokenservice.dto.JwtResponse;
import com.nhnacademy.tokenservice.provider.JwtProvider;
import com.nhnacademy.tokenservice.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements JwtTokenService {

    private static final String REDIS_KEY_TOKEN_PREFIX="refresh:user-email:";
    private static final String REDIS_KEY_BLACKLIST_PREFIX="blacklist:";
    private final RedisDao redisDao;
    private final JwtProvider provider;


    @Override
    public JwtResponse issueToken(String email){
        String accessToken = provider.createAccessToken(email);
        String refreshToken = provider.createRefreshToken(email);
        long expire = provider.getExpiration(refreshToken);

        redisDao.save(REDIS_KEY_TOKEN_PREFIX + email, refreshToken, expire);
        return new JwtResponse(accessToken, refreshToken);
    }

    @Override
    public JwtResponse reissueToken(String refreshToken) {
        String email = (String) provider.extractClaims(refreshToken).get("email");
        String key = REDIS_KEY_TOKEN_PREFIX + email;

        String storedRefreshToken = redisDao.get(key);

        if (!redisDao.has(key)) {
            throw new IllegalArgumentException("리프레시 토큰이 만료되었거나 존재하지 않습니다");
        }

        if (!storedRefreshToken.equals(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다");
        }

        String newAccessToken = provider.createAccessToken(email);
        String newRefreshToken = provider.createRefreshToken(email);
        long newExpire = provider.getExpiration(newRefreshToken);

        redisDao.delete(key);
        redisDao.save(key, newRefreshToken, newExpire); // 덮어쓰기 TTL 갱신 가능

        return new JwtResponse(newAccessToken, newRefreshToken);
    }

    // 나중에 필요
    @Override
    public void blackListToken(String accessToken) {
        long expire = provider.getExpiration(accessToken);
        String email = (String) provider.extractClaims(accessToken).get("email");
        String key = REDIS_KEY_TOKEN_PREFIX + email;

//        String hashed = DigestUtils.sha256Hex(accessToken);

        if(redisDao.has(key)){
            redisDao.delete(key);
        }

        redisDao.save(REDIS_KEY_BLACKLIST_PREFIX + accessToken, "logout", expire);

    }


}
