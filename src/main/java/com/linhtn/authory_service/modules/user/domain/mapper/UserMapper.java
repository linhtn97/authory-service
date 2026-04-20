package com.linhtn.authory_service.modules.user.domain.mapper;

import org.mapstruct.Mapper;

import com.linhtn.authory_service.config.auditor.AuditMapperConfig;
import com.linhtn.authory_service.modules.user.domain.dto.UserDto;
import com.linhtn.authory_service.modules.user.domain.entity.User;
import com.linhtn.shared_kernel.domain.mapper.BaseMapper;

@Mapper(componentModel = "spring", config = AuditMapperConfig.class)
public abstract class UserMapper implements BaseMapper<User, UserDto> {}
