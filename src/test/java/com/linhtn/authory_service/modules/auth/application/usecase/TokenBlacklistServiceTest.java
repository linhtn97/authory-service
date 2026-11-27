package com.linhtn.authory_service.modules.auth.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

class TokenBlacklistServiceTest {

    @Test
    @DisplayName("Should blacklist token in Redis when RedisTemplate is available")
    void shouldBlacklistTokenInRedis() {
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        TokenBlacklistService service = new TokenBlacklistService(redisTemplate);
        service.blacklistToken("jti-123", Duration.ofMinutes(15));

        verify(valueOperations)
                .set(
                        eq("authory:blacklist:jti:jti-123"),
                        eq("revoked"),
                        eq(Duration.ofMinutes(15)));
    }

    @Test
    @DisplayName("Should report isBlacklisted true when key exists in Redis")
    void shouldReportBlacklistedFromRedis() {
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        when(redisTemplate.hasKey("authory:blacklist:jti:jti-123")).thenReturn(Boolean.TRUE);

        TokenBlacklistService service = new TokenBlacklistService(redisTemplate);

        assertThat(service.isBlacklisted("jti-123")).isTrue();
        assertThat(service.isBlacklisted("jti-unknown")).isFalse();
    }

    @Test
    @DisplayName("Should use local fallback when Redis is null")
    void shouldUseLocalFallbackWhenRedisNull() {
        TokenBlacklistService service = new TokenBlacklistService(null);

        assertThat(service.isBlacklisted("jti-local")).isFalse();

        service.blacklistToken("jti-local", Duration.ofMinutes(5));
        assertThat(service.isBlacklisted("jti-local")).isTrue();
    }
}
