package com.linhtn.authory_service.modules.abac.application.port.in;

import com.linhtn.authory_service.modules.abac.application.dto.command.UserAttributeCreateCommand;
import com.linhtn.authory_service.modules.abac.application.dto.result.UserAttributeCreateResult;

public interface UserAttributeCreateUseCase {
    UserAttributeCreateResult execute(UserAttributeCreateCommand command);
}
