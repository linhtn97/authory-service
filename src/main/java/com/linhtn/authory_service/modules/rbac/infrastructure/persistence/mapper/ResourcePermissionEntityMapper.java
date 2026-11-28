package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.rbac.domain.model.Permission;
import com.linhtn.authory_service.modules.rbac.domain.model.Resource;
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
                .resource(
                        entity.getResourceId() != null
                                ? Resource.builder().id(entity.getResourceId()).build()
                                : null)
                .permission(
                        entity.getPermissionId() != null
                                ? Permission.builder().id(entity.getPermissionId()).build()
                                : null)
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
        if (domain.getResource() != null) {
            entity.setResourceId(domain.getResource().getId());
        }
        if (domain.getPermission() != null) {
            entity.setPermissionId(domain.getPermission().getId());
        }
        entity.setVersion(domain.getVersion());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        return entity;
    }
}
