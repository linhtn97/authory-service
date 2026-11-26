package com.linhtn.authory_service.modules.rbac.application.port.in;

import java.util.Set;
import java.util.UUID;

import com.linhtn.authory_service.domain.common.MappingManager;

public interface ResourcePermissionUseCase extends MappingManager<UUID, UUID> {
    Set<String> getPermissionCodesByResourceIdIn(Set<UUID> resourceIds);

    Set<UUID> getPermissionIdsByResourceId(UUID resourceId);
}
