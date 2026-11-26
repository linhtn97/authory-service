package com.linhtn.authory_service.modules.partner.application.port.in;

import java.util.UUID;

public interface PartnerDeleteUseCase {
    void execute(UUID id);
}
