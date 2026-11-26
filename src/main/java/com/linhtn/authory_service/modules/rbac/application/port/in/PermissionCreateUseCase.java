package com.linhtn.authory_service.modules.rbac.application.port.in;

import com.linhtn.authory_service.modules.rbac.application.dto.command.PermissionCreateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.PermissionCreateResult;

public interface PermissionCreateUseCase {
    PermissionCreateResult execute(PermissionCreateCommand command);
}
