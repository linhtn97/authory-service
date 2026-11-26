package com.linhtn.authory_service.modules.organization.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.organization.domain.model.Department;
import com.linhtn.authory_service.modules.organization.infrastructure.persistence.jpa.entity.DepartmentEntity;

public class DepartmentEntityMapper {

    private DepartmentEntityMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Department toDomain(DepartmentEntity entity) {
        if (entity == null) {
            return null;
        }
        return Department.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .branchId(entity.getBranchId())
                .centerId(entity.getCenterId())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public static DepartmentEntity toEntity(Department domain) {
        if (domain == null) {
            return null;
        }
        DepartmentEntity entity = new DepartmentEntity();
        entity.setId(domain.getId());
        entity.setCode(domain.getCode());
        entity.setName(domain.getName());
        entity.setBranchId(domain.getBranchId());
        entity.setCenterId(domain.getCenterId());
        entity.setVersion(domain.getVersion());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        return entity;
    }
}
