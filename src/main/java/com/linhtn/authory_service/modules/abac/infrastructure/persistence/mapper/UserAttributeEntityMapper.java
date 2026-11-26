package com.linhtn.authory_service.modules.abac.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.abac.domain.model.UserAttribute;
import com.linhtn.authory_service.modules.abac.infrastructure.persistence.jpa.entity.UserAttributeEntity;

public class UserAttributeEntityMapper {

    private UserAttributeEntityMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static UserAttribute toDomain(UserAttributeEntity entity) {
        if (entity == null) {
            return null;
        }
        return UserAttribute.builder()
                .id(entity.getId())
                .key(entity.getKey())
                .value(entity.getValue())
                .userId(entity.getUserId())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public static UserAttributeEntity toEntity(UserAttribute domain) {
        if (domain == null) {
            return null;
        }
        UserAttributeEntity entity = new UserAttributeEntity();
        entity.setId(domain.getId());
        entity.setKey(domain.getKey());
        entity.setValue(domain.getValue());
        entity.setUserId(domain.getUserId());
        entity.setVersion(domain.getVersion());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        return entity;
    }
}
