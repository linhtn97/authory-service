package com.linhtn.authory_service.modules.abac.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.abac.domain.model.Attribute;
import com.linhtn.authory_service.modules.abac.infrastructure.persistence.jpa.entity.AttributeEntity;

public class AttributeEntityMapper {

    private AttributeEntityMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Attribute toDomain(AttributeEntity entity) {
        if (entity == null) {
            return null;
        }
        return Attribute.builder()
                .id(entity.getId())
                .key(entity.getKey())
                .name(entity.getName())
                .description(entity.getDescription())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public static AttributeEntity toEntity(Attribute domain) {
        if (domain == null) {
            return null;
        }
        AttributeEntity entity = new AttributeEntity();
        entity.setId(domain.getId());
        entity.setKey(domain.getKey());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setVersion(domain.getVersion());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        return entity;
    }
}
