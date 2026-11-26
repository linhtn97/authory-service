package com.linhtn.authory_service.modules.auth.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.auth.domain.model.UserCredential;
import com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.entity.UserCredentialEntity;

public class UserCredentialEntityMapper {

    private UserCredentialEntityMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static UserCredential toDomain(UserCredentialEntity entity) {
        if (entity == null) {
            return null;
        }
        return UserCredential.builder()
                .id(entity.getId())
                .password(entity.getPassword())
                .userId(entity.getUserId())
                .failedLoginAttempts(
                        entity.getFailedLoginAttempts() != null
                                ? entity.getFailedLoginAttempts()
                                : 0)
                .lockedUntil(entity.getLockedUntil())
                .mfaEnabled(entity.getMfaEnabled() != null ? entity.getMfaEnabled() : false)
                .mfaSecret(entity.getMfaSecret())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public static UserCredentialEntity toEntity(UserCredential domain) {
        if (domain == null) {
            return null;
        }
        UserCredentialEntity entity = new UserCredentialEntity();
        entity.setId(domain.getId());
        entity.setPassword(domain.getPassword());
        entity.setUserId(domain.getUserId());
        entity.setFailedLoginAttempts(
                domain.getFailedLoginAttempts() != null ? domain.getFailedLoginAttempts() : 0);
        entity.setLockedUntil(domain.getLockedUntil());
        entity.setMfaEnabled(domain.getMfaEnabled() != null ? domain.getMfaEnabled() : false);
        entity.setMfaSecret(domain.getMfaSecret());
        entity.setVersion(domain.getVersion());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        return entity;
    }
}
