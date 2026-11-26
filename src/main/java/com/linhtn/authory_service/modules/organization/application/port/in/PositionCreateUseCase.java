package com.linhtn.authory_service.modules.organization.application.port.in;

import com.linhtn.authory_service.modules.organization.application.dto.command.PositionCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.PositionCreateResult;

public interface PositionCreateUseCase {
    PositionCreateResult execute(PositionCreateCommand command);
}
