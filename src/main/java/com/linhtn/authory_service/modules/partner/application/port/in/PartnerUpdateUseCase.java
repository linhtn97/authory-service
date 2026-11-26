package com.linhtn.authory_service.modules.partner.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.partner.application.dto.command.PartnerUpdateCommand;
import com.linhtn.authory_service.modules.partner.application.dto.result.PartnerUpdateResult;

public interface PartnerUpdateUseCase {
    PartnerUpdateResult execute(UUID id, PartnerUpdateCommand command);
}
