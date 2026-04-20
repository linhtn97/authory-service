package com.linhtn.authory_service.modules.rbac.service;

import java.util.List;
import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.domain.dto.RoleDto;
import com.linhtn.shared_kernel.domain.service.MappingManager;

public interface UserRoleService extends MappingManager<UUID, UUID> {
    List<RoleDto> getRoles(UUID userId);
}
