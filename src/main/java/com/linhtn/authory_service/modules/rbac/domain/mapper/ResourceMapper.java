package com.linhtn.authory_service.modules.rbac.domain.mapper;

import com.linhtn.authory_service.config.auditor.AuditMapperConfig;
import com.linhtn.authory_service.modules.rbac.domain.dto.ResourceDto;
import com.linhtn.authory_service.modules.rbac.domain.entity.Resource;
import com.linhtn.shared_kernel.domain.mapper.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = AuditMapperConfig.class)
public abstract class ResourceMapper implements BaseMapper<Resource, ResourceDto> {
}
