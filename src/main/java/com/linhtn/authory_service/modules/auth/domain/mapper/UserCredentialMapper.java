package com.linhtn.authory_service.modules.auth.domain.mapper;

import org.mapstruct.Mapper;

import com.linhtn.authory_service.config.auditor.AuditMapperConfig;
import com.linhtn.authory_service.modules.auth.domain.dto.UserCredentialDto;
import com.linhtn.authory_service.modules.auth.domain.entity.UserCredential;
import com.linhtn.shared_kernel.domain.mapper.BaseMapper;

@Mapper(componentModel = "spring", config = AuditMapperConfig.class)
public abstract class UserCredentialMapper
        implements BaseMapper<UserCredential, UserCredentialDto> {}
