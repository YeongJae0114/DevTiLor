package com.toy.devtilor.devtilor.auth.jwt;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "refresh:";

    public void saveToken(Long userId, String refreshToken, long expirationMillis) {
        redisTemplate.opsForValue().set(
                PREFIX + userId,            // ✅ refresh : 사용자 ID 저장
                refreshToken,                    // ✅ 토큰 자체를 value
                expirationMillis,
                TimeUnit.MILLISECONDS
        );
    }
    public void updateRefreshToken(Long userId,String oldRefreshToken ,String newRefreshToken){
        deleteToken(oldRefreshToken);
        saveToken(userId, newRefreshToken, 86400000L);
    }


    public String getToken(Long userId) {
        return redisTemplate.opsForValue().get(PREFIX + userId);
    }

    public void deleteToken(String refreshToken) {
        redisTemplate.delete(refreshToken);
    }

    public boolean isValid(Long userId, String refreshToken) {
        String saved = getToken(userId);
        return saved != null && saved.equals(refreshToken);
    }
}
