package com.linhtn.authory_service.modules.rbac.application.port.in;

import java.util.Set;
import java.util.UUID;

import com.linhtn.data_core.service.MappingManager;

public interface ResourcePermissionUseCase extends MappingManager<UUID, UUID> {
    Set<String> getPermissionCodesByResourceIdIn(Set<UUID> resourceIds);

    Set<UUID> getPermissionIdsByResourceId(UUID resourceId);
}
