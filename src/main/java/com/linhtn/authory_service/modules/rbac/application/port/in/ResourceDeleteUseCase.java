package com.linhtn.authory_service.modules.rbac.application.port.in;

import java.util.UUID;

public interface ResourceDeleteUseCase {
    void execute(UUID id);
}
