package com.linhtn.authory_service.modules.rbac.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.application.dto.result.GroupGetResult;

public interface GroupGetUseCase {
    GroupGetResult execute(UUID id);

    long count();
}
