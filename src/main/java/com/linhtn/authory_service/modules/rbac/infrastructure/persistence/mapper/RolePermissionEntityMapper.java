package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.rbac.domain.model.Permission;
import com.linhtn.authory_service.modules.rbac.domain.model.Role;
import com.linhtn.authory_service.modules.rbac.domain.model.RolePermission;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.RolePermissionEntity;

public final class RolePermissionEntityMapper {

    private RolePermissionEntityMapper() {}

    public static RolePermission toDomain(RolePermissionEntity entity) {
        if (entity == null) {
            return null;
        }
        return RolePermission.builder()
                .id(entity.getId())
                .role(
                        entity.getRoleId() != null
                                ? Role.builder().id(entity.getRoleId()).build()
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

    public static RolePermissionEntity toEntity(RolePermission domain) {
        if (domain == null) {
            return null;
        }
        RolePermissionEntity entity = new RolePermissionEntity();
        entity.setId(domain.getId());
        if (domain.getRole() != null) {
            entity.setRoleId(domain.getRole().getId());
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
