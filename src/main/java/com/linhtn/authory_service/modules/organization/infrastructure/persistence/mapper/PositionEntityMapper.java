package com.linhtn.authory_service.modules.organization.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.organization.domain.model.Position;
import com.linhtn.authory_service.modules.organization.infrastructure.persistence.jpa.entity.PositionEntity;

public class PositionEntityMapper {

    private PositionEntityMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Position toDomain(PositionEntity entity) {
        if (entity == null) {
            return null;
        }
        return Position.builder()
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

    public static PositionEntity toEntity(Position domain) {
        if (domain == null) {
            return null;
        }
        PositionEntity entity = new PositionEntity();
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
