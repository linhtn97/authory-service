package com.linhtn.authory_service.modules.organization.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.organization.domain.model.Center;
import com.linhtn.authory_service.modules.organization.infrastructure.persistence.jpa.entity.CenterEntity;

public class CenterEntityMapper {

    private CenterEntityMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Center toDomain(CenterEntity entity) {
        if (entity == null) {
            return null;
        }
        return Center.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .divisionId(entity.getDivisionId())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public static CenterEntity toEntity(Center domain) {
        if (domain == null) {
            return null;
        }
        CenterEntity entity = new CenterEntity();
        entity.setId(domain.getId());
        entity.setCode(domain.getCode());
        entity.setName(domain.getName());
        entity.setDivisionId(domain.getDivisionId());
        entity.setVersion(domain.getVersion());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        return entity;
    }
}
