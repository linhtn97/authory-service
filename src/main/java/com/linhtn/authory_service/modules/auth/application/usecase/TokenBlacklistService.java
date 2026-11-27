package com.linhtn.authory_service.modules.auth.application.usecase;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.linhtn.authory_service.modules.auth.application.port.in.TokenBlacklistUseCase;

@Service
public class TokenBlacklistService implements TokenBlacklistUseCase {
    private static final Logger log = LoggerFactory.getLogger(TokenBlacklistService.class);
    private static final String BLACKLIST_KEY_PREFIX = "authory:blacklist:jti:";

    private final StringRedisTemplate redisTemplate;
    // Local fallback in case Redis is unavailable during testing or dev
    private final Map<String, Long> localBlacklist = new ConcurrentHashMap<>();

    public TokenBlacklistService(@Autowired(required = false) StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void blacklistToken(String jti, Duration remainingTtl) {
        if (jti == null || jti.isBlank()) {
            return;
        }

        if (remainingTtl == null || remainingTtl.isNegative() || remainingTtl.isZero()) {
            remainingTtl = Duration.ofHours(1);
        }

        if (redisTemplate != null) {
            try {
                redisTemplate
                        .opsForValue()
                        .set(BLACKLIST_KEY_PREFIX + jti, "revoked", remainingTtl);
                log.info(
                        "Blacklisted token jti: {} in Redis for {} ms",
                        jti,
                        remainingTtl.toMillis());
                return;
            } catch (Exception e) {
                log.warn(
                        "Failed to blacklist jti in Redis, falling back to in-memory: {}",
                        e.getMessage());
            }
        }

        localBlacklist.put(jti, System.currentTimeMillis() + remainingTtl.toMillis());
        log.info("Blacklisted token jti: {} in local memory fallback", jti);
    }

    @Override
    public boolean isBlacklisted(String jti) {
        if (jti == null || jti.isBlank()) {
            return false;
        }

        if (redisTemplate != null) {
            try {
                Boolean exists = redisTemplate.hasKey(BLACKLIST_KEY_PREFIX + jti);
                return Boolean.TRUE.equals(exists);
            } catch (Exception e) {
                log.warn(
                        "Redis check failed for jti blacklist, checking local: {}", e.getMessage());
            }
        }

        Long expiry = localBlacklist.get(jti);
        if (expiry != null) {
            if (System.currentTimeMillis() < expiry) {
                return true;
            } else {
                localBlacklist.remove(jti);
            }
        }

        return false;
    }
}
