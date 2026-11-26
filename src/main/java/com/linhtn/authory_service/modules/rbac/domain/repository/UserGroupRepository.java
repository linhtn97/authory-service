package com.linhtn.authory_service.modules.rbac.domain.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.domain.model.UserGroup;

public interface UserGroupRepository {
    Set<UUID> findGroupIdsByUserId(UUID userId);

    void deleteByUserId(UUID userId);

    void deleteByUserIdAndGroupIds(UUID userId, Set<UUID> groupIds);

    UserGroup save(UserGroup userGroup);

    List<UserGroup> saveAll(List<UserGroup> userGroups);
}
