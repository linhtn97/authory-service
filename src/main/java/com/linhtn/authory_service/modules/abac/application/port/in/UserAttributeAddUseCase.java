package com.linhtn.authory_service.modules.abac.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.abac.application.dto.command.UserAttributeCreateCommand;

public interface UserAttributeAddUseCase {
    void execute(UUID userId, UserAttributeCreateCommand command);
}
