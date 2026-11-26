package com.linhtn.authory_service.modules.abac.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.abac.application.dto.command.AttributeUpdateCommand;
import com.linhtn.authory_service.modules.abac.application.dto.result.AttributeUpdateResult;

public interface AttributeUpdateUseCase {
    AttributeUpdateResult execute(UUID id, AttributeUpdateCommand command);
}
