package com.linhtn.authory_service.modules.rbac.application.port.in;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.linhtn.authory_service.domain.common.MappingManager;
import com.linhtn.authory_service.modules.rbac.application.dto.result.GroupGetResult;

public interface PartnerGroupUseCase extends MappingManager<UUID, UUID> {
    Set<String> getGroupCodes(UUID partnerId);

    Set<UUID> getGroupIds(UUID partnerId);

    List<GroupGetResult> getGroups(UUID partnerId);
}
