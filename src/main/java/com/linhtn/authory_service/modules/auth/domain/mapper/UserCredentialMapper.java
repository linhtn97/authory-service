package com.linhtn.authory_service.modules.auth.domain.mapper;

import com.linhtn.authory_service.config.auditor.AuditMapperConfig;
import com.linhtn.authory_service.modules.auth.domain.dto.UserCredentialDto;
import com.linhtn.authory_service.modules.auth.domain.entity.UserCredential;
import com.linhtn.shared_kernel.domain.mapper.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = AuditMapperConfig.class)
public abstract class UserCredentialMapper implements BaseMapper<UserCredential, UserCredentialDto> {
}
