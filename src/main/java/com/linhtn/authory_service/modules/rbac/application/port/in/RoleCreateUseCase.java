package com.linhtn.authory_service.modules.rbac.application.port.in;

import com.linhtn.authory_service.modules.rbac.application.dto.command.RoleCreateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.RoleCreateResult;

public interface RoleCreateUseCase {
    RoleCreateResult execute(RoleCreateCommand command);
}
