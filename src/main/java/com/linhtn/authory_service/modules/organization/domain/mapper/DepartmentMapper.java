package com.linhtn.authory_service.modules.organization.domain.mapper;

import com.linhtn.authory_service.config.auditor.AuditMapperConfig;
import com.linhtn.authory_service.modules.organization.domain.dto.DepartmentDto;
import com.linhtn.authory_service.modules.organization.domain.entity.Department;
import com.linhtn.shared_kernel.domain.mapper.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = AuditMapperConfig.class)
public abstract class DepartmentMapper implements BaseMapper<Department, DepartmentDto> {
}
