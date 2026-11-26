package com.linhtn.authory_service.modules.organization.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.organization.application.dto.command.BranchUpdateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.BranchUpdateResult;

public interface BranchUpdateUseCase {
    BranchUpdateResult execute(UUID id, BranchUpdateCommand command);
}
