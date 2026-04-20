package com.linhtn.authory_service.modules.abac.domain.mapper;

import org.mapstruct.Mapper;

import com.linhtn.authory_service.config.auditor.AuditMapperConfig;
import com.linhtn.authory_service.modules.abac.domain.dto.AttributeDto;
import com.linhtn.authory_service.modules.abac.domain.entity.Attribute;
import com.linhtn.shared_kernel.domain.mapper.BaseMapper;

@Mapper(componentModel = "spring", config = AuditMapperConfig.class)
public abstract class AttributeMapper implements BaseMapper<Attribute, AttributeDto> {}
