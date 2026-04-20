package com.linhtn.authory_service.modules.activity.domain.mapper;

import org.mapstruct.Mapper;

import com.linhtn.authory_service.config.auditor.AuditMapperConfig;
import com.linhtn.authory_service.modules.activity.domain.dto.SystemActivityDto;
import com.linhtn.authory_service.modules.activity.domain.entity.SystemActivity;
import com.linhtn.shared_kernel.domain.mapper.BaseMapper;

@Mapper(componentModel = "spring", config = AuditMapperConfig.class)
public abstract class SystemActivityMapper
        implements BaseMapper<SystemActivity, SystemActivityDto> {}
