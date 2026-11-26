package com.linhtn.authory_service.modules.rbac.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.application.dto.result.RoleGetResult;

public interface RoleGetUseCase {
    RoleGetResult execute(UUID id);

    long count();
}
