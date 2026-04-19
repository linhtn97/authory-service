package com.linhtn.authory_service.modules.rbac.service;

import com.linhtn.authory_service.modules.rbac.domain.dto.ResourceDto;
import com.linhtn.shared_kernel.domain.service.MappingManager;

import java.util.List;
import java.util.UUID;

public interface UserResourceService extends MappingManager<UUID, UUID> {
    List<ResourceDto> getResources(UUID userId);
}
