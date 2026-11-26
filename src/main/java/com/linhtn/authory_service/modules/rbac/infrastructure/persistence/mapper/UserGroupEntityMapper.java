package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.rbac.domain.model.UserGroup;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.UserGroupEntity;

public final class UserGroupEntityMapper {

    private UserGroupEntityMapper() {}

    public static UserGroup toDomain(UserGroupEntity entity) {
        if (entity == null) {
            return null;
        }
        return UserGroup.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .group(GroupEntityMapper.toDomain(entity.getGroup()))
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public static UserGroupEntity toEntity(UserGroup domain) {
        if (domain == null) {
            return null;
        }
        UserGroupEntity entity = new UserGroupEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setGroup(GroupEntityMapper.toEntity(domain.getGroup()));
        entity.setVersion(domain.getVersion());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        return entity;
    }
}
