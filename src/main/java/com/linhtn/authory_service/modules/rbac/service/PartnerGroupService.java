package com.linhtn.authory_service.modules.rbac.service;

import com.linhtn.authory_service.modules.rbac.domain.dto.GroupDto;
import com.linhtn.shared_kernel.domain.service.MappingManager;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PartnerGroupService extends MappingManager<UUID, UUID> {
    Set<String> getGroupCodes(UUID partnerId);

    Set<UUID> getGroupIds(UUID partnerId);

    List<GroupDto> getGroups(UUID partnerId);
}
