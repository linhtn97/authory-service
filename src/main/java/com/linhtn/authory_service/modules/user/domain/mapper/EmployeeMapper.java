package com.linhtn.authory_service.modules.user.domain.mapper;

import com.linhtn.authory_service.config.auditor.AuditMapperConfig;
import com.linhtn.authory_service.modules.user.domain.dto.EmployeeDto;
import com.linhtn.authory_service.modules.user.domain.entity.Employee;
import com.linhtn.shared_kernel.domain.mapper.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = AuditMapperConfig.class)
public abstract class EmployeeMapper implements BaseMapper<Employee, EmployeeDto> {
}
