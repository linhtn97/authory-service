package com.linhtn.authory_service.modules.auth.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.linhtn.authory_service.config.auditor.AuditMapperConfig;
import com.linhtn.authory_service.modules.auth.application.dto.result.UserCredentialResult;
import com.linhtn.authory_service.modules.auth.domain.model.UserCredential;

@Mapper(componentModel = "spring", config = AuditMapperConfig.class)
public abstract class UserCredentialMapper {
    public abstract UserCredentialResult toDto(UserCredential entity);

    public abstract UserCredential toEntity(UserCredentialResult dto);

    public abstract void fillEntity(@MappingTarget UserCredential entity, UserCredentialResult dto);
}
