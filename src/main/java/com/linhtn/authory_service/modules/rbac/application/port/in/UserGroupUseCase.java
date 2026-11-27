package com.linhtn.authory_service.modules.rbac.application.port.in;

import java.util.List;
import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.application.dto.result.GroupGetResult;
import com.linhtn.data_core.service.MappingManager;

public interface UserGroupUseCase extends MappingManager<UUID, UUID> {
    List<GroupGetResult> getGroups(UUID userId);
}
