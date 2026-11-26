package com.linhtn.authory_service.modules.rbac.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.application.dto.command.PermissionUpdateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.PermissionUpdateResult;

public interface PermissionUpdateUseCase {
    PermissionUpdateResult execute(UUID id, PermissionUpdateCommand command);
}
