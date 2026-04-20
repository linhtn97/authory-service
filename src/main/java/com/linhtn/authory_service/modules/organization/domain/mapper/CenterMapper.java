package com.linhtn.authory_service.modules.organization.domain.mapper;

import org.mapstruct.Mapper;

import com.linhtn.authory_service.config.auditor.AuditMapperConfig;
import com.linhtn.authory_service.modules.organization.domain.dto.CenterDto;
import com.linhtn.authory_service.modules.organization.domain.entity.Center;
import com.linhtn.shared_kernel.domain.mapper.BaseMapper;

@Mapper(componentModel = "spring", config = AuditMapperConfig.class)
public abstract class CenterMapper implements BaseMapper<Center, CenterDto> {}
