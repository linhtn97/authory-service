package com.linhtn.authory_service.modules.rbac.application.port.in;

import java.util.List;
import java.util.UUID;

import com.linhtn.authory_service.domain.common.MappingManager;
import com.linhtn.authory_service.modules.rbac.application.dto.result.GroupGetResult;

public interface UserGroupUseCase extends MappingManager<UUID, UUID> {
    List<GroupGetResult> getGroups(UUID userId);
}
