package com.linhtn.authory_service.modules.organization.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.organization.application.dto.command.PositionUpdateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.PositionUpdateResult;

public interface PositionUpdateUseCase {
    PositionUpdateResult execute(UUID id, PositionUpdateCommand command);
}
