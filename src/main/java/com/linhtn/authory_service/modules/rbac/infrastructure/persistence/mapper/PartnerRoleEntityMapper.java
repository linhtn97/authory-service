package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.rbac.domain.model.PartnerRole;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.PartnerRoleEntity;

public final class PartnerRoleEntityMapper {

    private PartnerRoleEntityMapper() {}

    public static PartnerRole toDomain(PartnerRoleEntity entity) {
        if (entity == null) {
            return null;
        }
        return PartnerRole.builder()
                .id(entity.getId())
                .partnerId(entity.getPartnerId())
                .role(RoleEntityMapper.toDomain(entity.getRole()))
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public static PartnerRoleEntity toEntity(PartnerRole domain) {
        if (domain == null) {
            return null;
        }
        PartnerRoleEntity entity = new PartnerRoleEntity();
        entity.setId(domain.getId());
        entity.setPartnerId(domain.getPartnerId());
        entity.setRole(RoleEntityMapper.toEntity(domain.getRole()));
        entity.setVersion(domain.getVersion());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        return entity;
    }
}
