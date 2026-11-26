package com.linhtn.authory_service.modules.organization.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.organization.application.dto.command.CenterUpdateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.CenterUpdateResult;

public interface CenterUpdateUseCase {
    CenterUpdateResult execute(UUID id, CenterUpdateCommand command);
}
