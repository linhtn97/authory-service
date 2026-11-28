package com.linhtn.authory_service.modules.auth.infrastructure.persistence;

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

class RedisTokenBlacklistAdapterTest {

    @Test
    @DisplayName("Should blacklist token in Redis when RedisTemplate is available")
    void shouldBlacklistTokenInRedis() {
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        RedisTokenBlacklistAdapter adapter = new RedisTokenBlacklistAdapter(redisTemplate);
        adapter.blacklistToken("jti-123", Duration.ofMinutes(15));

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

        RedisTokenBlacklistAdapter adapter = new RedisTokenBlacklistAdapter(redisTemplate);

        assertThat(adapter.isBlacklisted("jti-123")).isTrue();
        assertThat(adapter.isBlacklisted("jti-unknown")).isFalse();
    }

    @Test
    @DisplayName("Should use local fallback when Redis is null")
    void shouldUseLocalFallbackWhenRedisNull() {
        RedisTokenBlacklistAdapter adapter = new RedisTokenBlacklistAdapter(null);

        assertThat(adapter.isBlacklisted("jti-local")).isFalse();

        adapter.blacklistToken("jti-local", Duration.ofMinutes(5));
        assertThat(adapter.isBlacklisted("jti-local")).isTrue();
    }
}
