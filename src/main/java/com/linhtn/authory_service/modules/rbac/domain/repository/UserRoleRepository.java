package com.linhtn.authory_service.modules.rbac.domain.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.domain.model.UserRole;

public interface UserRoleRepository {
    Set<UUID> findRoleIdsByUserId(UUID userId);

    void deleteByUserId(UUID userId);

    void deleteByUserIdAndRoleIds(UUID userId, Set<UUID> roleIds);

    UserRole save(UserRole userRole);

    List<UserRole> saveAll(List<UserRole> userRoles);
}
