package com.linhtn.authory_service.modules.abac.application.port.in;

import java.util.UUID;

public interface UserAttributeDeleteUseCase {
    void execute(UUID id);
}
