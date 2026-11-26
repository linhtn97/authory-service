package com.linhtn.authory_service.modules.organization.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.organization.application.dto.command.CompanyUpdateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.CompanyUpdateResult;

public interface CompanyUpdateUseCase {
    CompanyUpdateResult execute(UUID id, CompanyUpdateCommand command);
}
