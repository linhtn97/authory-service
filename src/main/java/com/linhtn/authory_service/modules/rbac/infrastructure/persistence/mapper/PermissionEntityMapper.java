package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.rbac.domain.model.Permission;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.PermissionEntity;

public final class PermissionEntityMapper {

    private PermissionEntityMapper() {}

    public static Permission toDomain(PermissionEntity entity) {
        if (entity == null) {
            return null;
        }
        return Permission.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .action(entity.getAction())
                .type(entity.getType())
                .name(entity.getName())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public static PermissionEntity toEntity(Permission domain) {
        if (domain == null) {
            return null;
        }
        PermissionEntity entity = new PermissionEntity();
        entity.setId(domain.getId());
        entity.setCode(domain.getCode());
        entity.setAction(domain.getAction());
        entity.setType(domain.getType());
        entity.setName(domain.getName());
        entity.setVersion(domain.getVersion());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        return entity;
    }
}
