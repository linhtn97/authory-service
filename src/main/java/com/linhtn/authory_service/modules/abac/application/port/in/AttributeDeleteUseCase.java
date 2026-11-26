package com.linhtn.authory_service.modules.abac.application.port.in;

import java.util.UUID;

public interface AttributeDeleteUseCase {
    void execute(UUID id);
}
