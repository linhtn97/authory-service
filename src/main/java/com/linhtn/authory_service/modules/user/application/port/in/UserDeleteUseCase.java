package com.linhtn.authory_service.modules.user.application.port.in;

import java.util.UUID;

public interface UserDeleteUseCase {
    void execute(UUID id);
}
