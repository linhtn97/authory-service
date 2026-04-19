package com.linhtn.authory_service.modules.auth.domain.mapper;

import com.linhtn.authory_service.config.auditor.AuditMapperConfig;
import com.linhtn.authory_service.modules.auth.domain.dto.ApiCredentialDto;
import com.linhtn.authory_service.modules.auth.domain.entity.ApiCredential;
import com.linhtn.shared_kernel.domain.mapper.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = AuditMapperConfig.class)
public abstract class ApiCredentialMapper implements BaseMapper<ApiCredential, ApiCredentialDto> {
}
