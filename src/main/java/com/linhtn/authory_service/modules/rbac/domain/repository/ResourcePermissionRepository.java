package com.linhtn.authory_service.modules.rbac.domain.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.domain.model.ResourcePermission;

public interface ResourcePermissionRepository {
    Set<UUID> findPermissionIdsByResourceId(UUID resourceId);

    Set<String> findPermissionCodesByResourceIdIn(Set<UUID> resourceIds);

    void deleteByResourceId(UUID resourceId);

    void deleteByResourceIdAndPermissionIds(UUID resourceId, Set<UUID> permissionIds);

    ResourcePermission save(ResourcePermission resourcePermission);

    List<ResourcePermission> saveAll(List<ResourcePermission> resourcePermissions);
}
