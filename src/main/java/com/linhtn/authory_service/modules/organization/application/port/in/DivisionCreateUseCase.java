package com.linhtn.authory_service.modules.organization.application.port.in;

import com.linhtn.authory_service.modules.organization.application.dto.command.DivisionCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.DivisionCreateResult;

public interface DivisionCreateUseCase {
    DivisionCreateResult execute(DivisionCreateCommand command);
}
