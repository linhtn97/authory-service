package com.linhtn.authory_service.modules.organization.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.organization.application.dto.command.DivisionUpdateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.DivisionUpdateResult;

public interface DivisionUpdateUseCase {
    DivisionUpdateResult execute(UUID id, DivisionUpdateCommand command);
}
