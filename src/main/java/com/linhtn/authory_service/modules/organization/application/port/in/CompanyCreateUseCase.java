package com.linhtn.authory_service.modules.organization.application.port.in;

import com.linhtn.authory_service.modules.organization.application.dto.command.CompanyCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.CompanyCreateResult;

public interface CompanyCreateUseCase {
    CompanyCreateResult execute(CompanyCreateCommand command);
}
