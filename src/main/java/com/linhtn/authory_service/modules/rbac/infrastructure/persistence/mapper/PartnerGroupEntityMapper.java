package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.rbac.domain.model.PartnerGroup;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.PartnerGroupEntity;

public final class PartnerGroupEntityMapper {

    private PartnerGroupEntityMapper() {}

    public static PartnerGroup toDomain(PartnerGroupEntity entity) {
        if (entity == null) {
            return null;
        }
        return PartnerGroup.builder()
                .id(entity.getId())
                .partnerId(entity.getPartnerId())
                .group(GroupEntityMapper.toDomain(entity.getGroup()))
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public static PartnerGroupEntity toEntity(PartnerGroup domain) {
        if (domain == null) {
            return null;
        }
        PartnerGroupEntity entity = new PartnerGroupEntity();
        entity.setId(domain.getId());
        entity.setPartnerId(domain.getPartnerId());
        entity.setGroup(GroupEntityMapper.toEntity(domain.getGroup()));
        entity.setVersion(domain.getVersion());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        return entity;
    }
}
