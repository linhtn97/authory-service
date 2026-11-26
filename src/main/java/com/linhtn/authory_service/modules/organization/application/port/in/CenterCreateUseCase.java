package com.linhtn.authory_service.modules.organization.application.port.in;

import com.linhtn.authory_service.modules.organization.application.dto.command.CenterCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.CenterCreateResult;

public interface CenterCreateUseCase {
    CenterCreateResult execute(CenterCreateCommand command);
}
