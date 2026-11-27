package com.linhtn.authory_service.modules.rbac.application.port.in;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.application.dto.result.RoleGetResult;
import com.linhtn.data_core.service.MappingManager;

public interface PartnerRoleUseCase extends MappingManager<UUID, UUID> {
    Set<String> getRoleCodes(UUID partnerId);

    Set<UUID> getRoleIds(UUID partnerId);

    List<RoleGetResult> getRoles(UUID partnerId);
}
