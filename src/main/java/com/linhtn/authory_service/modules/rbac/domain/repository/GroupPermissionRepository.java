package com.linhtn.authory_service.modules.rbac.domain.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.domain.model.GroupPermission;

public interface GroupPermissionRepository {
    Set<UUID> findPermissionIdsByGroupId(UUID groupId);

    Set<String> findPermissionCodesByGroupIdIn(Set<UUID> groupIds);

    void deleteByGroupId(UUID groupId);

    void deleteByGroupIdAndPermissionIds(UUID groupId, Set<UUID> permissionIds);

    GroupPermission save(GroupPermission groupPermission);

    List<GroupPermission> saveAll(List<GroupPermission> groupPermissions);
}
