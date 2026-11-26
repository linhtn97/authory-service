package com.linhtn.authory_service.modules.organization.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.organization.domain.model.Division;
import com.linhtn.authory_service.modules.organization.infrastructure.persistence.jpa.entity.DivisionEntity;

public class DivisionEntityMapper {

    private DivisionEntityMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Division toDomain(DivisionEntity entity) {
        if (entity == null) {
            return null;
        }
        return Division.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public static DivisionEntity toEntity(Division domain) {
        if (domain == null) {
            return null;
        }
        DivisionEntity entity = new DivisionEntity();
        entity.setId(domain.getId());
        entity.setCode(domain.getCode());
        entity.setName(domain.getName());
        entity.setVersion(domain.getVersion());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        return entity;
    }
}
