package com.linhtn.authory_service.modules.auth.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.linhtn.authory_service.config.auditor.AuditMapperConfig;
import com.linhtn.authory_service.modules.auth.application.dto.result.TokenResult;
import com.linhtn.authory_service.modules.auth.domain.model.Token;

@Mapper(componentModel = "spring", config = AuditMapperConfig.class)
public abstract class TokenApplicationMapper {
    public abstract TokenResult toDto(Token entity);

    public abstract Token toEntity(TokenResult dto);

    public abstract void fillEntity(@MappingTarget Token entity, TokenResult dto);
}
