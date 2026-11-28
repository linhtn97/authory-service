package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.rbac.domain.model.Resource;
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
                .resource(
                        entity.getResourceId() != null
                                ? Resource.builder().id(entity.getResourceId()).build()
                                : null)
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
        if (domain.getResource() != null) {
            entity.setResourceId(domain.getResource().getId());
        }
        entity.setVersion(domain.getVersion());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        return entity;
    }
}
