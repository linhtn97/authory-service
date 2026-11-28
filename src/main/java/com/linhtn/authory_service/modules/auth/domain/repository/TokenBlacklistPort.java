package com.linhtn.authory_service.modules.auth.domain.repository;

import java.time.Duration;

public interface TokenBlacklistPort {

    void blacklistToken(String jti, Duration remainingTtl);

    boolean isBlacklisted(String jti);
}
