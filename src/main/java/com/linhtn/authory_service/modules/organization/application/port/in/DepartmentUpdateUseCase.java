package com.linhtn.authory_service.modules.organization.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.organization.application.dto.command.DepartmentUpdateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.DepartmentUpdateResult;

public interface DepartmentUpdateUseCase {
    DepartmentUpdateResult execute(UUID id, DepartmentUpdateCommand command);
}
