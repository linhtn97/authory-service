package com.linhtn.authory_service.modules.rbac.application.port.in;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.linhtn.authory_service.domain.common.MappingManager;
import com.linhtn.authory_service.modules.rbac.application.dto.result.ResourceGetResult;

public interface PartnerResourceUseCase extends MappingManager<UUID, UUID> {
    Set<String> getResourceCodes(UUID partnerId);

    Set<UUID> getResourceIds(UUID partnerId);

    List<ResourceGetResult> getResources(UUID partnerId);
}
