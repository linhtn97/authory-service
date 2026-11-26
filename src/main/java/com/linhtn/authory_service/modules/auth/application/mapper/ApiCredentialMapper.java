package com.linhtn.authory_service.modules.auth.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.linhtn.authory_service.config.auditor.AuditMapperConfig;
import com.linhtn.authory_service.modules.auth.application.dto.result.ApiCredentialResult;
import com.linhtn.authory_service.modules.auth.domain.model.ApiCredential;

@Mapper(componentModel = "spring", config = AuditMapperConfig.class)
public abstract class ApiCredentialMapper {
    public abstract ApiCredentialResult toDto(ApiCredential entity);

    public abstract ApiCredential toEntity(ApiCredentialResult dto);

    public abstract void fillEntity(@MappingTarget ApiCredential entity, ApiCredentialResult dto);
}
