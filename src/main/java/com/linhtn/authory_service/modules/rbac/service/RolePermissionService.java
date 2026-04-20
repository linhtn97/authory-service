package com.linhtn.authory_service.modules.rbac.service;

import java.util.Set;
import java.util.UUID;

import com.linhtn.shared_kernel.domain.service.MappingManager;

public interface RolePermissionService extends MappingManager<UUID, UUID> {
    Set<String> getPermissionCodesByRoleIdIn(Set<UUID> roleIds);

    Set<UUID> getPermissionIdsByRoleId(UUID roleId);
}
