package com.linhtn.authory_service.modules.auth.application.usecase;

import java.time.Duration;

import org.springframework.stereotype.Service;

import com.linhtn.authory_service.modules.auth.application.port.in.TokenBlacklistUseCase;
import com.linhtn.authory_service.modules.auth.domain.repository.TokenBlacklistPort;

@Service
public class TokenBlacklistService implements TokenBlacklistUseCase {

    private final TokenBlacklistPort tokenBlacklistPort;

    public TokenBlacklistService(TokenBlacklistPort tokenBlacklistPort) {
        this.tokenBlacklistPort = tokenBlacklistPort;
    }

    @Override
    public void blacklistToken(String jti, Duration remainingTtl) {
        tokenBlacklistPort.blacklistToken(jti, remainingTtl);
    }

    @Override
    public boolean isBlacklisted(String jti) {
        return tokenBlacklistPort.isBlacklisted(jti);
    }
}
