package com.linhtn.authory_service.modules.rbac.service;

import com.linhtn.authory_service.modules.rbac.domain.dto.GroupDto;
import com.linhtn.shared_kernel.domain.service.MappingManager;

import java.util.List;
import java.util.UUID;

public interface UserGroupService extends MappingManager<UUID, UUID> {
    List<GroupDto> getGroups(UUID userId);
}
