package com.linhtn.authory_service.modules.rbac.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.domain.dto.RoleDto;
import com.linhtn.shared_kernel.domain.service.MappingManager;

public interface PartnerRoleService extends MappingManager<UUID, UUID> {
    Set<String> getRoleCodes(UUID partnerId);

    Set<UUID> getRoleIds(UUID partnerId);

    List<RoleDto> getRoles(UUID partnerId);
}
