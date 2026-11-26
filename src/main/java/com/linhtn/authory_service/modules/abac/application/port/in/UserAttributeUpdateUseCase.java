package com.linhtn.authory_service.modules.abac.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.abac.application.dto.command.UserAttributeUpdateCommand;
import com.linhtn.authory_service.modules.abac.application.dto.result.UserAttributeUpdateResult;

public interface UserAttributeUpdateUseCase {
    UserAttributeUpdateResult execute(UUID id, UserAttributeUpdateCommand command);
}
