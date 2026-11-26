package com.linhtn.authory_service.modules.rbac.application.port.in;

import com.linhtn.authory_service.modules.rbac.application.dto.command.GroupCreateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.GroupCreateResult;

public interface GroupCreateUseCase {
    GroupCreateResult execute(GroupCreateCommand command);
}
