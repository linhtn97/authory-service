package com.linhtn.authory_service.modules.abac.domain.mapper;

import com.linhtn.authory_service.config.auditor.AuditMapperConfig;
import com.linhtn.authory_service.modules.abac.domain.dto.UserAttributeDto;
import com.linhtn.authory_service.modules.abac.domain.entity.UserAttribute;
import com.linhtn.shared_kernel.domain.mapper.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = AuditMapperConfig.class)
public abstract class UserAttributeMapper implements BaseMapper<UserAttribute, UserAttributeDto> {
}
