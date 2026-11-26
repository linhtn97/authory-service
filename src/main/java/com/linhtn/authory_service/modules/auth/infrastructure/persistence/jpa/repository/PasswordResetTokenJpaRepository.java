package com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.entity.PasswordResetTokenEntity;

@Repository
public interface PasswordResetTokenJpaRepository
        extends JpaRepository<PasswordResetTokenEntity, UUID> {
    Optional<PasswordResetTokenEntity> findByTokenHashAndUsedIsFalse(String tokenHash);
}
