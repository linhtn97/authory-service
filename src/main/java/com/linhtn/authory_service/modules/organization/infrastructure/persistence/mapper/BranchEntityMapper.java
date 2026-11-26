package com.linhtn.authory_service.modules.organization.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.organization.domain.model.Branch;
import com.linhtn.authory_service.modules.organization.infrastructure.persistence.jpa.entity.BranchEntity;

public class BranchEntityMapper {

    private BranchEntityMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Branch toDomain(BranchEntity entity) {
        if (entity == null) {
            return null;
        }
        return Branch.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .companyId(entity.getCompanyId())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public static BranchEntity toEntity(Branch domain) {
        if (domain == null) {
            return null;
        }
        BranchEntity entity = new BranchEntity();
        entity.setId(domain.getId());
        entity.setCode(domain.getCode());
        entity.setName(domain.getName());
        entity.setCompanyId(domain.getCompanyId());
        entity.setVersion(domain.getVersion());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        return entity;
    }
}
