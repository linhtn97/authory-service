package com.linhtn.authory_service.modules.rbac.service;

import java.util.Set;
import java.util.UUID;

import com.linhtn.shared_kernel.domain.service.MappingManager;

public interface GroupPermissionService extends MappingManager<UUID, UUID> {
    Set<String> getPermissionCodesByGroupIdIn(Set<UUID> groupIds);

    Set<UUID> getPermissionIdsByGroupId(UUID groupId);
}
