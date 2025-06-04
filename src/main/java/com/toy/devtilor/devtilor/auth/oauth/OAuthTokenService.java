package com.toy.devtilor.devtilor.auth.oauth;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthTokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "google:";

    public void saveToken(Long userId, String refreshToken, long expirationMillis) {
        redisTemplate.opsForValue().set(
                PREFIX + userId,          // ✅ refresh : 사용자 ID 저장
                refreshToken,                    // ✅ 토큰 자체를 value 로
                expirationMillis,
                TimeUnit.MILLISECONDS
        );
    }
}