package com.linhtn.authory_service.modules.user.domain.mapper;

import com.linhtn.authory_service.config.auditor.AuditMapperConfig;
import com.linhtn.authory_service.modules.user.domain.dto.UserDto;
import com.linhtn.authory_service.modules.user.domain.entity.User;
import com.linhtn.shared_kernel.domain.mapper.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = AuditMapperConfig.class)
public abstract class UserMapper implements BaseMapper<User, UserDto> {
}
