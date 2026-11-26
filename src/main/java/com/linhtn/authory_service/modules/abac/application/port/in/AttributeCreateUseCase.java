package com.linhtn.authory_service.modules.abac.application.port.in;

import com.linhtn.authory_service.modules.abac.application.dto.command.AttributeCreateCommand;
import com.linhtn.authory_service.modules.abac.application.dto.result.AttributeCreateResult;

public interface AttributeCreateUseCase {
    AttributeCreateResult execute(AttributeCreateCommand command);
}
