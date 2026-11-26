package com.linhtn.authory_service.modules.partner.application.port.in;

import com.linhtn.authory_service.modules.partner.application.dto.command.PartnerCreateCommand;
import com.linhtn.authory_service.modules.partner.application.dto.result.PartnerCreateResult;

public interface PartnerCreateUseCase {
    PartnerCreateResult execute(PartnerCreateCommand command);
}
