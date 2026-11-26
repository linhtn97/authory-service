package com.linhtn.authory_service.modules.organization.application.port.in;

import com.linhtn.authory_service.modules.organization.application.dto.command.BranchCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.BranchCreateResult;

public interface BranchCreateUseCase {
    BranchCreateResult execute(BranchCreateCommand command);
}
