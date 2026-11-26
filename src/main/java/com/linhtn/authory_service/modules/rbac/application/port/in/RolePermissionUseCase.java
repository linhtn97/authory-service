package com.linhtn.authory_service.modules.rbac.application.port.in;

import java.util.Set;
import java.util.UUID;

import com.linhtn.authory_service.domain.common.MappingManager;

public interface RolePermissionUseCase extends MappingManager<UUID, UUID> {
    Set<String> getPermissionCodesByRoleIdIn(Set<UUID> roleIds);

    Set<UUID> getPermissionIdsByRoleId(UUID roleId);
}
