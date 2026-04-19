package com.linhtn.authory_service.modules.rbac.service;

import com.linhtn.shared_kernel.domain.service.MappingManager;

import java.util.Set;
import java.util.UUID;

public interface ResourcePermissionService extends MappingManager<UUID, UUID> {
    Set<String> getPermissionCodesByResourceIdIn(Set<UUID> resourceIds);

    Set<UUID> getPermissionIdsByResourceId(UUID resourceId);

}
