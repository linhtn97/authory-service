package com.linhtn.authory_service.modules.rbac.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.application.dto.command.GroupUpdateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.GroupUpdateResult;

public interface GroupUpdateUseCase {
    GroupUpdateResult execute(UUID id, GroupUpdateCommand command);
}
