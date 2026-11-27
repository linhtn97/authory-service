package com.linhtn.authory_service.modules.auth.application.port.in;

import java.time.Duration;

public interface TokenBlacklistUseCase {

    void blacklistToken(String jti, Duration remainingTtl);

    boolean isBlacklisted(String jti);
}
