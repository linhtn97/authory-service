package com.linhtn.authory_service.modules.rbac.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.application.dto.command.RoleUpdateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.RoleUpdateResult;

public interface RoleUpdateUseCase {
    RoleUpdateResult execute(UUID id, RoleUpdateCommand command);
}
