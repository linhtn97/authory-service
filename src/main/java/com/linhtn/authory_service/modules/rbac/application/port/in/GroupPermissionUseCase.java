package com.linhtn.authory_service.modules.rbac.application.port.in;

import java.util.Set;
import java.util.UUID;

import com.linhtn.data_core.service.MappingManager;

public interface GroupPermissionUseCase extends MappingManager<UUID, UUID> {
    Set<String> getPermissionCodesByGroupIdIn(Set<UUID> groupIds);

    Set<UUID> getPermissionIdsByGroupId(UUID groupId);
}
