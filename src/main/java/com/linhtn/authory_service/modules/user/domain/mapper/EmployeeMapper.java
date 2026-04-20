package com.linhtn.authory_service.modules.user.domain.mapper;

import org.mapstruct.Mapper;

import com.linhtn.authory_service.config.auditor.AuditMapperConfig;
import com.linhtn.authory_service.modules.user.domain.dto.EmployeeDto;
import com.linhtn.authory_service.modules.user.domain.entity.Employee;
import com.linhtn.shared_kernel.domain.mapper.BaseMapper;

@Mapper(componentModel = "spring", config = AuditMapperConfig.class)
public abstract class EmployeeMapper implements BaseMapper<Employee, EmployeeDto> {}
