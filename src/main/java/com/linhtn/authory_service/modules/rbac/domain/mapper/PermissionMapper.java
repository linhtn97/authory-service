package com.linhtn.authory_service.modules.rbac.domain.mapper;

import org.mapstruct.Mapper;

import com.linhtn.authory_service.config.auditor.AuditMapperConfig;
import com.linhtn.authory_service.modules.rbac.domain.dto.PermissionDto;
import com.linhtn.authory_service.modules.rbac.domain.entity.Permission;
import com.linhtn.shared_kernel.domain.mapper.BaseMapper;

@Mapper(componentModel = "spring", config = AuditMapperConfig.class)
public abstract class PermissionMapper implements BaseMapper<Permission, PermissionDto> {}
