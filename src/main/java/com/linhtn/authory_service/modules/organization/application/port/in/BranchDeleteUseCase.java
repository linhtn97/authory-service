package com.linhtn.authory_service.modules.organization.application.port.in;

import java.util.UUID;

public interface BranchDeleteUseCase {
    void execute(UUID id);
}
