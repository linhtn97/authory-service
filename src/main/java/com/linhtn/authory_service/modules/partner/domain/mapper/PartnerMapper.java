package com.linhtn.authory_service.modules.partner.domain.mapper;

import com.linhtn.authory_service.config.auditor.AuditMapperConfig;
import com.linhtn.authory_service.modules.partner.domain.dto.PartnerDto;
import com.linhtn.authory_service.modules.partner.domain.entity.Partner;
import com.linhtn.shared_kernel.domain.mapper.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = AuditMapperConfig.class)
public abstract class PartnerMapper implements BaseMapper<Partner, PartnerDto> {
}
