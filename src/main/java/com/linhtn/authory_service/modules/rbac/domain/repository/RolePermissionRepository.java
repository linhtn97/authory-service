package com.linhtn.authory_service.modules.rbac.domain.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.domain.model.RolePermission;

public interface RolePermissionRepository {
    Set<UUID> findPermissionIdsByRoleId(UUID roleId);

    Set<String> findPermissionCodesByRoleIdIn(Set<UUID> roleIds);

    void deleteByRoleId(UUID roleId);

    void deleteByRoleIdAndPermissionIds(UUID roleId, Set<UUID> permissionIds);

    RolePermission save(RolePermission rolePermission);

    List<RolePermission> saveAll(List<RolePermission> rolePermissions);
}
