package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.rbac.domain.model.ResourcePermission;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.ResourcePermissionEntity;

public final class ResourcePermissionEntityMapper {

    private ResourcePermissionEntityMapper() {}

    public static ResourcePermission toDomain(ResourcePermissionEntity entity) {
        if (entity == null) {
            return null;
        }
        return ResourcePermission.builder()
                .id(entity.getId())
                .resource(ResourceEntityMapper.toDomain(entity.getResource()))
                .permission(PermissionEntityMapper.toDomain(entity.getPermission()))
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public static ResourcePermissionEntity toEntity(ResourcePermission domain) {
        if (domain == null) {
            return null;
        }
        ResourcePermissionEntity entity = new ResourcePermissionEntity();
        entity.setId(domain.getId());
        entity.setResource(ResourceEntityMapper.toEntity(domain.getResource()));
        entity.setPermission(PermissionEntityMapper.toEntity(domain.getPermission()));
        entity.setVersion(domain.getVersion());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        return entity;
    }
}
