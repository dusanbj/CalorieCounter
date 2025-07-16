package rs.fon.demo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "blacklisted:";

    public void blacklistToken(String token, long expirationMillis) {
        redisTemplate.opsForValue().set(
                PREFIX + token,
                "true",
                Duration.ofMillis(expirationMillis)
        );
    }

    public boolean isTokenBlacklisted(String token) {
        Boolean exists = redisTemplate.hasKey(PREFIX + token);
        return exists != null && exists;
    }
}