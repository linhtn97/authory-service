package com.linhtn.authory_service.modules.rbac.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.application.dto.command.ResourceUpdateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.ResourceUpdateResult;

public interface ResourceUpdateUseCase {
    ResourceUpdateResult execute(UUID id, ResourceUpdateCommand command);
}
