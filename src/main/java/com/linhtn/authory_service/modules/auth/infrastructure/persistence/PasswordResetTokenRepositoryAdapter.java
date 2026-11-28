package com.linhtn.authory_service.modules.auth.infrastructure.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.auth.domain.model.PasswordResetToken;
import com.linhtn.authory_service.modules.auth.domain.repository.PasswordResetTokenRepository;
import com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.entity.PasswordResetTokenEntity;
import com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.repository.PasswordResetTokenJpaRepository;
import com.linhtn.authory_service.modules.auth.infrastructure.persistence.mapper.PasswordResetTokenEntityMapper;

@Repository
public class PasswordResetTokenRepositoryAdapter implements PasswordResetTokenRepository {

    private final PasswordResetTokenJpaRepository jpaRepository;
    private final PasswordResetTokenEntityMapper mapper;

    public PasswordResetTokenRepositoryAdapter(
            PasswordResetTokenJpaRepository jpaRepository, PasswordResetTokenEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<PasswordResetToken> findByTokenHashAndUsedIsFalse(String tokenHash) {
        return jpaRepository.findByTokenHashAndUsedIsFalse(tokenHash).map(mapper::toDomain);
    }

    @Override
    public PasswordResetToken save(PasswordResetToken passwordResetToken) {
        PasswordResetTokenEntity entity = mapper.toEntity(passwordResetToken);
        PasswordResetTokenEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}
