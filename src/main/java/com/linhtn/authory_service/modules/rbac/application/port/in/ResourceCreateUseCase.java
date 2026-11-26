package com.linhtn.authory_service.modules.rbac.application.port.in;

import com.linhtn.authory_service.modules.rbac.application.dto.command.ResourceCreateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.ResourceCreateResult;

public interface ResourceCreateUseCase {
    ResourceCreateResult execute(ResourceCreateCommand command);
}
