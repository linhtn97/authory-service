package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.rbac.domain.model.Group;
import com.linhtn.authory_service.modules.rbac.domain.model.GroupPermission;
import com.linhtn.authory_service.modules.rbac.domain.model.Permission;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.GroupPermissionEntity;

public final class GroupPermissionEntityMapper {

    private GroupPermissionEntityMapper() {}

    public static GroupPermission toDomain(GroupPermissionEntity entity) {
        if (entity == null) {
            return null;
        }
        return GroupPermission.builder()
                .id(entity.getId())
                .group(
                        entity.getGroupId() != null
                                ? Group.builder().id(entity.getGroupId()).build()
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

    public static GroupPermissionEntity toEntity(GroupPermission domain) {
        if (domain == null) {
            return null;
        }
        GroupPermissionEntity entity = new GroupPermissionEntity();
        entity.setId(domain.getId());
        if (domain.getGroup() != null) {
            entity.setGroupId(domain.getGroup().getId());
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
