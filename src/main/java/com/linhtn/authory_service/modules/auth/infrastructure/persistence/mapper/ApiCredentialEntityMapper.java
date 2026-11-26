package com.linhtn.authory_service.modules.auth.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.auth.domain.model.ApiCredential;
import com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.entity.ApiCredentialEntity;

public class ApiCredentialEntityMapper {

    private ApiCredentialEntityMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static ApiCredential toDomain(ApiCredentialEntity entity) {
        if (entity == null) {
            return null;
        }
        return ApiCredential.builder()
                .id(entity.getId())
                .clientId(entity.getClientId())
                .hashedSecret(entity.getHashedSecret())
                .apiKey(entity.getApiKey())
                .status(entity.getStatus())
                .revoked(entity.getRevoked())
                .expiredAt(entity.getExpiredAt())
                .partnerId(entity.getPartnerId())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public static ApiCredentialEntity toEntity(ApiCredential domain) {
        if (domain == null) {
            return null;
        }
        ApiCredentialEntity entity = new ApiCredentialEntity();
        entity.setId(domain.getId());
        entity.setClientId(domain.getClientId());
        entity.setHashedSecret(domain.getHashedSecret());
        entity.setApiKey(domain.getApiKey());
        entity.setStatus(domain.getStatus());
        entity.setRevoked(domain.getRevoked());
        entity.setExpiredAt(domain.getExpiredAt());
        entity.setPartnerId(domain.getPartnerId());
        entity.setVersion(domain.getVersion());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        return entity;
    }
}
