package com.linhtn.authory_service.modules.organization.application.port.in;

import com.linhtn.authory_service.modules.organization.application.dto.command.DepartmentCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.DepartmentCreateResult;

public interface DepartmentCreateUseCase {
    DepartmentCreateResult execute(DepartmentCreateCommand command);
}
