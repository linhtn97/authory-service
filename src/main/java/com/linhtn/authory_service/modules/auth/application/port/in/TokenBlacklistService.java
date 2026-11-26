package com.linhtn.authory_service.modules.auth.application.port.in;

import java.time.Duration;

public interface TokenBlacklistService {

    void blacklistToken(String jti, Duration remainingTtl);

    boolean isBlacklisted(String jti);
}
