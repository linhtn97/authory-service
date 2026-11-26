package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.rbac.domain.model.PartnerResource;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.PartnerResourceEntity;

public final class PartnerResourceEntityMapper {

    private PartnerResourceEntityMapper() {}

    public static PartnerResource toDomain(PartnerResourceEntity entity) {
        if (entity == null) {
            return null;
        }
        return PartnerResource.builder()
                .id(entity.getId())
                .partnerId(entity.getPartnerId())
                .resource(ResourceEntityMapper.toDomain(entity.getResource()))
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public static PartnerResourceEntity toEntity(PartnerResource domain) {
        if (domain == null) {
            return null;
        }
        PartnerResourceEntity entity = new PartnerResourceEntity();
        entity.setId(domain.getId());
        entity.setPartnerId(domain.getPartnerId());
        entity.setResource(ResourceEntityMapper.toEntity(domain.getResource()));
        entity.setVersion(domain.getVersion());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        return entity;
    }
}
