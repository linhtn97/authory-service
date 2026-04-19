package com.linhtn.authory_service.modules.auth.domain.mapper;

import com.linhtn.authory_service.config.auditor.AuditMapperConfig;
import com.linhtn.authory_service.modules.auth.domain.dto.TokenDto;
import com.linhtn.authory_service.modules.auth.domain.entity.Token;
import com.linhtn.shared_kernel.domain.mapper.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = AuditMapperConfig.class)
public abstract class TokenMapper implements BaseMapper<Token, TokenDto> {
}
