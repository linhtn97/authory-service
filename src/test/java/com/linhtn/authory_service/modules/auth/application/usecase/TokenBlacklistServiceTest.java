package com.linhtn.authory_service.modules.auth.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.linhtn.authory_service.modules.auth.domain.repository.TokenBlacklistPort;

class TokenBlacklistServiceTest {

    @Test
    @DisplayName("Should delegate blacklistToken to TokenBlacklistPort")
    void shouldDelegateBlacklistToken() {
        TokenBlacklistPort port = mock(TokenBlacklistPort.class);
        TokenBlacklistService service = new TokenBlacklistService(port);

        service.blacklistToken("jti-123", Duration.ofMinutes(15));
        verify(port).blacklistToken("jti-123", Duration.ofMinutes(15));
    }

    @Test
    @DisplayName("Should delegate isBlacklisted to TokenBlacklistPort")
    void shouldDelegateIsBlacklisted() {
        TokenBlacklistPort port = mock(TokenBlacklistPort.class);
        when(port.isBlacklisted("jti-123")).thenReturn(true);
        when(port.isBlacklisted("jti-unknown")).thenReturn(false);

        TokenBlacklistService service = new TokenBlacklistService(port);

        assertThat(service.isBlacklisted("jti-123")).isTrue();
        assertThat(service.isBlacklisted("jti-unknown")).isFalse();
    }
}
