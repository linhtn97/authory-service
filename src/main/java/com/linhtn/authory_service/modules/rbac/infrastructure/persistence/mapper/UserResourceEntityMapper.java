package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.rbac.domain.model.UserResource;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.UserResourceEntity;

public final class UserResourceEntityMapper {

    private UserResourceEntityMapper() {}

    public static UserResource toDomain(UserResourceEntity entity) {
        if (entity == null) {
            return null;
        }
        return UserResource.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .resource(ResourceEntityMapper.toDomain(entity.getResource()))
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public static UserResourceEntity toEntity(UserResource domain) {
        if (domain == null) {
            return null;
        }
        UserResourceEntity entity = new UserResourceEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setResource(ResourceEntityMapper.toEntity(domain.getResource()));
        entity.setVersion(domain.getVersion());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        return entity;
    }
}
