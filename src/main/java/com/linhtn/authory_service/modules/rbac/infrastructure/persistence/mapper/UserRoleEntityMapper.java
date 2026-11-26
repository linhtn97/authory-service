package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.rbac.domain.model.UserRole;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.UserRoleEntity;

public final class UserRoleEntityMapper {

    private UserRoleEntityMapper() {}

    public static UserRole toDomain(UserRoleEntity entity) {
        if (entity == null) {
            return null;
        }
        return UserRole.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .role(RoleEntityMapper.toDomain(entity.getRole()))
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public static UserRoleEntity toEntity(UserRole domain) {
        if (domain == null) {
            return null;
        }
        UserRoleEntity entity = new UserRoleEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setRole(RoleEntityMapper.toEntity(domain.getRole()));
        entity.setVersion(domain.getVersion());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        return entity;
    }
}
