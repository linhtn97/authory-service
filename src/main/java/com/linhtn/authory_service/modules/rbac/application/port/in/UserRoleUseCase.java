package com.linhtn.authory_service.modules.rbac.application.port.in;

import java.util.List;
import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.application.dto.result.RoleGetResult;
import com.linhtn.data_core.service.MappingManager;

public interface UserRoleUseCase extends MappingManager<UUID, UUID> {
    List<RoleGetResult> getRoles(UUID userId);
}
