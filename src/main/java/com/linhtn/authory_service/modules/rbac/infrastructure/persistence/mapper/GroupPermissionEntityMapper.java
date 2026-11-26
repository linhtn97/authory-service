package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.rbac.domain.model.GroupPermission;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.GroupPermissionEntity;

public final class GroupPermissionEntityMapper {

    private GroupPermissionEntityMapper() {}

    public static GroupPermission toDomain(GroupPermissionEntity entity) {
        if (entity == null) {
            return null;
        }
        return GroupPermission.builder()
                .id(entity.getId())
                .group(GroupEntityMapper.toDomain(entity.getGroup()))
                .permission(PermissionEntityMapper.toDomain(entity.getPermission()))
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
        entity.setGroup(GroupEntityMapper.toEntity(domain.getGroup()));
        entity.setPermission(PermissionEntityMapper.toEntity(domain.getPermission()));
        entity.setVersion(domain.getVersion());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        return entity;
    }
}
