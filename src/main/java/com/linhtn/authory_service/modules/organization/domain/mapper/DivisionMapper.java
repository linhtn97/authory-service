package com.linhtn.authory_service.modules.organization.domain.mapper;

import org.mapstruct.Mapper;

import com.linhtn.authory_service.config.auditor.AuditMapperConfig;
import com.linhtn.authory_service.modules.organization.domain.dto.DivisionDto;
import com.linhtn.authory_service.modules.organization.domain.entity.Division;
import com.linhtn.shared_kernel.domain.mapper.BaseMapper;

@Mapper(componentModel = "spring", config = AuditMapperConfig.class)
public abstract class DivisionMapper implements BaseMapper<Division, DivisionDto> {}
