package com.linhtn.authory_service.modules.partner.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.partner.application.dto.result.PartnerGetResult;

public interface PartnerGetUseCase {
    PartnerGetResult execute(UUID id);
}
