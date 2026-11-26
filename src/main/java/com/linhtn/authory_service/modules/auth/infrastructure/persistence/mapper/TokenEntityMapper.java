package com.linhtn.authory_service.modules.auth.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.auth.domain.model.Token;
import com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.entity.TokenEntity;

public class TokenEntityMapper {

    private TokenEntityMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Token toDomain(TokenEntity entity) {
        if (entity == null) {
            return null;
        }
        return Token.builder()
                .id(entity.getId())
                .jti(entity.getJti())
                .type(entity.getType())
                .revoked(entity.getRevoked())
                .expiredAt(entity.getExpiredAt())
                .userId(entity.getUserId())
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .deviceName(entity.getDeviceName())
                .lastActiveAt(entity.getLastActiveAt())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public static TokenEntity toEntity(Token domain) {
        if (domain == null) {
            return null;
        }
        TokenEntity entity = new TokenEntity();
        entity.setId(domain.getId());
        entity.setJti(domain.getJti());
        entity.setType(domain.getType());
        entity.setRevoked(domain.getRevoked());
        entity.setExpiredAt(domain.getExpiredAt());
        entity.setUserId(domain.getUserId());
        entity.setIpAddress(domain.getIpAddress());
        entity.setUserAgent(domain.getUserAgent());
        entity.setDeviceName(domain.getDeviceName());
        entity.setLastActiveAt(domain.getLastActiveAt());
        entity.setVersion(domain.getVersion());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        return entity;
    }
}
