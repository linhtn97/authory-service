package com.linhtn.authory_service.config.auditor;

import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.linhtn.shared_kernel.infrastructure.jpa.entity.BaseEntity;

@MapperConfig
public interface AuditMapperConfig {
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void fillEntity(@MappingTarget BaseEntity entity, Object dto);
}
