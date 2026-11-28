package com.linhtn.authory_service.modules.auth.domain.repository;

import java.util.Optional;

import com.linhtn.authory_service.modules.auth.domain.model.PasswordResetToken;

public interface PasswordResetTokenRepository {
    Optional<PasswordResetToken> findByTokenHashAndUsedIsFalse(String tokenHash);

    PasswordResetToken save(PasswordResetToken passwordResetToken);
}
