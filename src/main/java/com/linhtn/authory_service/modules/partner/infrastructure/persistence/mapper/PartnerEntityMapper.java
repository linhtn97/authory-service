package com.linhtn.authory_service.modules.partner.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.partner.domain.model.Partner;
import com.linhtn.authory_service.modules.partner.infrastructure.persistence.jpa.entity.PartnerEntity;

public class PartnerEntityMapper {

    private PartnerEntityMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Partner toDomain(PartnerEntity entity) {
        if (entity == null) {
            return null;
        }
        return Partner.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .name(entity.getName())
                .phone(entity.getPhone())
                .address(entity.getAddress())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public static PartnerEntity toEntity(Partner domain) {
        if (domain == null) {
            return null;
        }
        PartnerEntity entity = new PartnerEntity();
        entity.setId(domain.getId());
        entity.setUsername(domain.getUsername());
        entity.setEmail(domain.getEmail());
        entity.setName(domain.getName());
        entity.setPhone(domain.getPhone());
        entity.setAddress(domain.getAddress());
        entity.setVersion(domain.getVersion());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        return entity;
    }
}
